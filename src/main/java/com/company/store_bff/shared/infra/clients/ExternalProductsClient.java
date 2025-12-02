package com.company.store_bff.shared.infra.clients;

import com.company.store_bff.products.domain.models.Product;
import com.company.store_bff.products.domain.ports.out.ExternalProductServicePort;
import com.company.store_bff.shared.infra.config.AppConfigEnvironment;
import com.company.store_bff.shared.infra.dtos.ExternalProductDetail;
import com.company.store_bff.shared.infra.mappers.ExternalProductDetailMapper;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.isNull;

@Component
@AllArgsConstructor
@Slf4j
public class ExternalProductsClient implements ExternalProductServicePort {

    private final WebClient webClient;
    private final AppConfigEnvironment appConfigEnvironment;
    private final ExternalProductDetailMapper externalProductDetailMapper;
    private final Cache<String, Product> productDetailsCache;

    @Override
    public Mono<List<String>> getSimilarProductsIds(String productId) {
        log.debug("getSimilarProductsIds - Fetching similar IDs for product {}", productId);
        return webClient.get()
                .uri(appConfigEnvironment.getExternalProductsServiceUriProductSimilarIds(), productId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {});
    }

    @Override
    public Flux<Product> getProductsDetails(List<String> productIds) {
        log.debug("getProductsDetail - Fetching details for products {}", productIds);
        if (isNull(productIds) || productIds.isEmpty()) {
            return Flux.empty();
        }

        return Flux.fromIterable(productIds)
                .flatMap(this::getProductDetailWithCache, appConfigEnvironment.getMaxConcurrentRequestsProductDetail());
    }

    private Mono<Product> getProductDetailWithCache(String productId) {
        return Mono.justOrEmpty(productDetailsCache.getIfPresent(productId))
                .doOnNext(cachedProduct -> log.debug("Cache HIT for product {}", productId))
                .switchIfEmpty(
                        Mono.defer(() -> {
                            log.debug("Cache MISS for product {} - Fetching from external service", productId);
                            return fetchProductDetailFromExternalService(productId)
                                    .doOnNext(product -> {
                                        log.debug("Storing product {} in cache", productId);
                                        productDetailsCache.put(productId, product);
                                    });
                        })
                );
    }

    private Mono<Product> fetchProductDetailFromExternalService(String productId) {
        return webClient.get()
                .uri(appConfigEnvironment.getExternalProductsServiceUriProductDetail(), productId)
                .retrieve()
                .bodyToMono(ExternalProductDetail.class)
                .map(externalProductDetailMapper::toDomain)
                .onErrorResume(error -> {
                    log.warn("Error fetching product {}: {}", productId, error.getMessage(), error);
                    return Mono.empty();
                });
    }

}
