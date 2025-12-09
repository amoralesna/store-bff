package com.company.store_bff.products.infra.clients;

import com.company.store_bff.products.domain.models.Product;
import com.company.store_bff.products.infra.clients.ExternalProductsClient;
import com.company.store_bff.products.infra.config.AppConfigEnvironment;
import com.company.store_bff.products.infra.dtos.ExternalProductDetail;
import com.company.store_bff.products.infra.mappers.ExternalProductDetailMapper;
import com.github.benmanes.caffeine.cache.Cache;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalProductsClientTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient webClientMock;

    @Mock
    private AppConfigEnvironment appConfigEnvironmentMock;

    @Mock
    private ExternalProductDetailMapper externalProductDetailMapperMock;

    @Mock
    private Cache<String, Product> productDetailsCacheMock;

    private CircuitBreakerRegistry circuitBreakerRegistry;
    private RateLimiterRegistry rateLimiterRegistry;
    private TimeLimiterRegistry timeLimiterRegistry;

    private ExternalProductsClient externalProductsClient;

    @BeforeEach
    void setUp() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowSize(100)
                .failureRateThreshold(100)
                .build();
        circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);

        RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .limitForPeriod(500)
                .timeoutDuration(Duration.ofMillis(0))
                .build();
        rateLimiterRegistry = RateLimiterRegistry.of(rateLimiterConfig);

        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofMinutes(1))
                .build();
        timeLimiterRegistry = TimeLimiterRegistry.of(timeLimiterConfig);

        externalProductsClient = new ExternalProductsClient(
                webClientMock,
                appConfigEnvironmentMock,
                externalProductDetailMapperMock,
                productDetailsCacheMock,
                circuitBreakerRegistry,
                rateLimiterRegistry,
                timeLimiterRegistry
        );
    }

    @Test
    void should_getSimilarProductsIds_return_list() {

        when(appConfigEnvironmentMock.getExternalProductsServiceUriProductSimilarIds())
                .thenReturn("/product/{id}/similarids");

        when(webClientMock.get().uri(anyString(), anyString()).retrieve().bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(List.of("1", "2", "3")));

        Mono<List<String>> result = externalProductsClient.getSimilarProductsIds("1");

        StepVerifier.create(result)
                .expectNextMatches(ids -> ids.size() == 3 && ids.equals(List.of("1", "2", "3")))
                .verifyComplete();
    }

    @Test
    void should_getProductsDetail_return_flux() {
        when(appConfigEnvironmentMock.getExternalProductsServiceUriProductDetail())
                .thenReturn("/product/{id}");

        when(appConfigEnvironmentMock.getMaxConcurrentRequestsProductDetail())
                .thenReturn(2);

        when(productDetailsCacheMock.getIfPresent(anyString()))
                .thenReturn(null);

        ExternalProductDetail detail1 = ExternalProductDetail.builder().id("1").name("Product 1").build();
        ExternalProductDetail detail2 = ExternalProductDetail.builder().id("2").name("Product 2").build();

        when(webClientMock.get().uri(anyString(), eq("1")).retrieve().bodyToMono(eq(ExternalProductDetail.class)))
                .thenReturn(Mono.just(detail1));

        when(webClientMock.get().uri(anyString(), eq("2")).retrieve().bodyToMono(eq(ExternalProductDetail.class)))
                .thenReturn(Mono.just(detail2));

        Product product1 = new Product("1", "Product 1", null, false);
        Product product2 = new Product("2", "Product 2", null, false);

        when(externalProductDetailMapperMock.toDomain(detail1)).thenReturn(product1);
        when(externalProductDetailMapperMock.toDomain(detail2)).thenReturn(product2);

        Flux<Product> result = externalProductsClient.getProductsDetails(List.of("1", "2"));

        StepVerifier.create(result)
                .expectNext(product1, product2)
                .verifyComplete();

        verify(externalProductDetailMapperMock, times(2)).toDomain(any());
        verify(productDetailsCacheMock, times(2)).put(anyString(), any(Product.class));
    }

    @Test
    void should_getProductsDetail_return_empty_when_list_is_null() {

        Flux<Product> result = externalProductsClient.getProductsDetails(null);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    void should_getProductsDetail_return_empty_when_list_is_empty() {
        when(appConfigEnvironmentMock.getMaxConcurrentRequestsProductDetail())
                .thenReturn(2);

        Flux<Product> result = externalProductsClient.getProductsDetails(List.of());

        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }
}
