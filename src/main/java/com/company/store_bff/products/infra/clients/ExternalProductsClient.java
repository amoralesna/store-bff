package com.company.store_bff.products.infra.clients;

import com.company.store_bff.products.domain.models.Product;
import com.company.store_bff.products.domain.ports.out.ExternalProductServicePort;
import com.company.store_bff.products.infra.config.AppConfigEnvironment;
import com.company.store_bff.products.infra.dtos.ExternalProductDetail;
import com.company.store_bff.products.infra.mappers.ExternalProductDetailMapper;
import com.github.benmanes.caffeine.cache.Cache;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.timelimiter.TimeLimiterOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@Slf4j
public class ExternalProductsClient implements ExternalProductServicePort {

    private final WebClient webClient;
    private final AppConfigEnvironment appConfigEnvironment;
    private final ExternalProductDetailMapper externalProductDetailMapper;
    private final Cache<String, Product> productDetailsCache;
    private final CircuitBreaker circuitBreaker;
    private final Bulkhead bulkhead;
    private final TimeLimiter timeLimiter;

    public ExternalProductsClient(WebClient webClient,
                                  AppConfigEnvironment appConfigEnvironment,
                                  ExternalProductDetailMapper externalProductDetailMapper,
                                  Cache<String, Product> productDetailsCache,
                                  CircuitBreakerRegistry circuitBreakerRegistry,
                                  BulkheadRegistry bulkheadRegistry,
                                  TimeLimiterRegistry timeLimiterRegistry) {
        this.webClient = webClient;
        this.appConfigEnvironment = appConfigEnvironment;
        this.externalProductDetailMapper = externalProductDetailMapper;
        this.productDetailsCache = productDetailsCache;

        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("productDetails");
        this.circuitBreaker.getEventPublisher()
                .onStateTransition(event -> log.warn("CircuitBreaker 'productDetails' changed state: {}", event))
                .onError(event -> log.error("CircuitBreaker 'productDetails' error: {}", event.getThrowable().getMessage()));

        this.bulkhead = bulkheadRegistry.bulkhead("productDetails");
        this.bulkhead.getEventPublisher()
                .onCallPermitted(event -> log.debug("Bulkhead 'productDetails' call permitted"))
                .onCallRejected(event -> log.warn("Bulkhead 'productDetails' call REJECTED - Max concurrent calls reached"))
                .onCallFinished(event -> log.debug("Bulkhead 'productDetails' call finished"));

        this.timeLimiter = timeLimiterRegistry.timeLimiter("productDetails");
        this.timeLimiter.getEventPublisher()
                .onSuccess(event -> log.debug("TimeLimiter 'productDetails' call succeeded"))
                .onTimeout(event -> log.warn("TimeLimiter 'productDetails' call TIMEOUT"))
                .onError(event -> log.error("TimeLimiter 'productDetails' call ERROR: {}", event.getThrowable().getMessage()));

    }

    @Override
    public Mono<List<String>> getSimilarProductsIds(String productId) {
        log.debug("Fetching similar IDs for product {}", productId);
        return webClient.get()
                .uri(appConfigEnvironment.getExternalProductsServiceUriProductSimilarIds(), productId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }

    @Override
    public Flux<Product> getProductsDetails(List<String> productIds) {
        if (isNull(productIds)) {
            log.debug("ProductIds is null, returning empty flux");
            return Flux.empty();
        }
        log.debug("Fetching details for products {}", productIds);
        return Flux.fromIterable(productIds)
                .flatMap(this::getProductDetailWithCache, appConfigEnvironment.getMaxConcurrentRequestsProductDetail());
    }

    private Mono<Product> getProductDetailWithCache(String productId) {
        Product cached = productDetailsCache.getIfPresent(productId);
        if (nonNull(cached)) {
            log.debug("Cache HIT for product {}", productId);
            return Mono.just(cached);
        }

        log.debug("Cache MISS for product {}", productId);
        return fetchProductDetailFromExternalService(productId)
                .doOnNext(product -> productDetailsCache.put(productId, product));
    }

    private Mono<Product> fetchProductDetailFromExternalService(String productId) {
        return webClient.get()
                .uri(appConfigEnvironment.getExternalProductsServiceUriProductDetail(), productId)
                .retrieve()
                .bodyToMono(ExternalProductDetail.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .transformDeferred(BulkheadOperator.of(bulkhead))
                .transformDeferred(TimeLimiterOperator.of(timeLimiter))
                .map(externalProductDetailMapper::toDomain)
                .onErrorResume(error -> {
                    log.warn("Error fetching product {}: {}", productId, error.getMessage());
                    return Mono.empty();
                });
    }

}
