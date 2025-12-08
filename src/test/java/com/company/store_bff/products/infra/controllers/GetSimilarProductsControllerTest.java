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
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

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
        Product p1 = new Product("2", "Product 2", 20.0, true);
        Product p2 = new Product("3", "Product 3", 30.0, false);

        ProductDetail pd1 = new ProductDetail("2", "Product 2", BigDecimal.valueOf(20), true);
        ProductDetail pd2 = new ProductDetail("3", "Product 3", BigDecimal.valueOf(30), false);

        when(getSimilarProductsUseCase.execute("1")).thenReturn(Flux.just(p1, p2));
        when(productMapper.toDto(p1)).thenReturn(pd1);
        when(productMapper.toDto(p2)).thenReturn(pd2);

        StepVerifier.create(controller.getProductSimilar("1"))
                .expectNextMatches(responseEntity ->
                        responseEntity.getStatusCode().is2xxSuccessful() &&
                                responseEntity.getBody() != null &&
                                responseEntity.getBody().size() == 2)
                .verifyComplete();

        verify(productMapper, times(1)).toDto(p1);
        verify(productMapper, times(1)).toDto(p2);
    }

    @Test
    void should_return_empty_list_when_usecase_returns_empty_flux() {
        when(getSimilarProductsUseCase.execute("2")).thenReturn(Flux.empty());

        StepVerifier.create(controller.getProductSimilar("2"))
                .expectNextMatches(responseEntity ->
                        responseEntity.getStatusCode().is2xxSuccessful() &&
                                responseEntity.getBody() != null &&
                                responseEntity.getBody().isEmpty())
                .verifyComplete();

        verifyNoInteractions(productMapper);
    }
}
