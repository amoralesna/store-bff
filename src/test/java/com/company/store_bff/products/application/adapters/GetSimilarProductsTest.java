package com.company.store_bff.products.application.adapters;


import com.company.store_bff.products.application.adapters.GetSimilarProducts;
import com.company.store_bff.products.domain.models.Product;
import com.company.store_bff.products.domain.ports.out.ExternalProductServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class GetSimilarProductsTest {

    @Mock
    private ExternalProductServicePort externalProductServicePort;

    @InjectMocks
    private GetSimilarProducts getSimilarProductsUseCase;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void should_getSimilarProductList() {
        String productId = "1";

        when(externalProductServicePort
                .getSimilarProductsIds(anyString()))
                .thenReturn(Mono.just(List.of("2", "3", "4")));

        when(externalProductServicePort
                .getProductsDetails(anyList()))
                .thenReturn(Flux.fromIterable(getProductList()));

        Mono<Set<Product>> result = getSimilarProductsUseCase.execute(productId);

        StepVerifier.create(result)
                .expectNextMatches(products -> products.size() == 3)
                .verifyComplete();
    }

    @Test
    void should_returnEmptySet_when_noSimilarProductsFound() {
        String productId = "1";

        when(externalProductServicePort
                .getSimilarProductsIds(anyString()))
                .thenReturn(Mono.just(List.of()));

        when(externalProductServicePort
                .getProductsDetails(anyList()))
                .thenReturn(Flux.empty());

        Mono<Set<Product>> result = getSimilarProductsUseCase.execute(productId);

        StepVerifier.create(result)
                .expectNextMatches(Set::isEmpty)
                .verifyComplete();
    }

    private static List<Product> getProductList() {
        return List.of(
                new Product("2", "Product 2", 20.0, true),
                new Product("3", "Product 3", 30.0, false),
                new Product("4", "Product 4", 40.0, true)
        );
    }
}