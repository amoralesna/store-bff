package com.company.store_bff.shared.infra.adapters;

import com.company.store_bff.products.domain.models.Product;
import com.company.store_bff.products.domain.ports.out.ExternalProductServicePort;
import com.company.store_bff.shared.infra.config.AppConfigEnvironment;
import com.company.store_bff.shared.infra.dtos.ExternalProductDetail;
import com.company.store_bff.shared.infra.mappers.ExternalProductDetailMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class ExternalProductsServiceAdapter implements ExternalProductServicePort {

    public static final int CONCURRENCY = 10;

    private final WebClient webClient;
    private final AppConfigEnvironment appConfigEnvironment;
    private final ExternalProductDetailMapper externalProductDetailMapper;


    @Override
    public List<String> getSimilarProductsIds(String productId) {

        return webClient.get()
                .uri(appConfigEnvironment.getExternalProductsServiceUriProductSimilarIds(), productId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .onErrorResume(e -> {
                    log.error("Error fetching similar ids for product {}: {}", productId, e.getMessage(), e);
                    return Mono.just(List.of());
                })
                .block();
    }

    @Override
    public List<Product> getProductsDetail(List<String> productIds) {


        return Flux.fromIterable(productIds)
                .flatMap(productId ->
                    webClient.get()
                        .uri(appConfigEnvironment.getExternalProductsServiceUriProductDetail(), productId)
                        .retrieve()
                        .bodyToMono(ExternalProductDetail.class)
                        .onErrorResume(e -> {
                            log.error("Error fetching product detail for id {}: {}", productId, e.getMessage(), e);
                            return Mono.empty();
                        })
                        .map(externalProductDetailMapper::toDomain),
                    CONCURRENCY
                )
                .collectList()
                .block();

    }
}
