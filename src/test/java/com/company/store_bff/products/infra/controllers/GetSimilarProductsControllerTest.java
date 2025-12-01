package com.company.store_bff.products.infra.controllers;

import com.company.store_bff.products.domain.models.Product;
import com.company.store_bff.products.domain.ports.in.GetSimilarProductsUseCase;
import com.company.store_bff.shared.infra.api.model.ProductDetail;
import com.company.store_bff.shared.infra.mappers.ProductMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSimilarProductsControllerTest {

    @Mock
    private GetSimilarProductsUseCase getSimilarProductsUseCase;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private GetSimilarProductsController controller;

    @Test
    void should_getProductSimilar_return_response_with_mapped_details() {
        Product p = new Product("p1", "Product 1", 10.0, true);
        Set<Product> domainSet = Set.of(p);
        Set<ProductDetail> mapped = Set.of(new ProductDetail("p1", "Product 1", BigDecimal.valueOf(10), true));

        when(getSimilarProductsUseCase.execute("p1")).thenReturn(Mono.just(domainSet));
        when(productMapper.toResponse(domainSet)).thenReturn(mapped);

        StepVerifier.create(controller.getProductSimilar("p1"))
                .expectNextMatches(responseEntity ->
                        responseEntity.getStatusCode().is2xxSuccessful() &&
                                responseEntity.getBody() == mapped)
                .verifyComplete();

        verify(productMapper, times(1)).toResponse(domainSet);
    }

    @Test
    void should_complete_without_value_when_usecase_returns_empty() {
        when(getSimilarProductsUseCase.execute("p2")).thenReturn(Mono.empty());

        StepVerifier.create(controller.getProductSimilar("p2"))
                .expectComplete()
                .verify();

        verifyNoInteractions(productMapper);
    }

//    @Test
//    void should_propagate_illegal_argument_exception_when_usecase_errors_with_illegal_argument_exception() {
//        IllegalArgumentException ex = new IllegalArgumentException("Product ID must not be null or empty");
//        when(getSimilarProductsUseCase.execute("p3")).thenReturn(Mono.error(ex));
//
//        StepVerifier.create(controller.getProductSimilar("p3"))
//                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
//                        throwable.getMessage().contains("must not be null"))
//                .verify();
//
//        verifyNoInteractions(productMapper);
//    }
}
