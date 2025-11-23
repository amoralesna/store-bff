package com.company.store_bff.products.application.usecases;


import com.company.store_bff.products.application.adapters.GetSimilarProductsAdapter;
import com.company.store_bff.products.domain.models.Product;
import com.company.store_bff.products.domain.ports.out.ExternalProductServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class GetSimilarProductsAdapterTest {

    @Mock
    private ExternalProductServicePort externalProductServicePort;

    @InjectMocks
    private GetSimilarProductsAdapter getSimilarProductsUseCase;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void should_getSimilarProductList() {
        String productId = "1";

        when(externalProductServicePort
                .getSimilarProductsIds(anyString()))
                .thenReturn(List.of("2", "3", "4"));

        when(externalProductServicePort
                .getProductsDetail(anyList()))
                .thenReturn(getProductList());

        Set<Product> similarProductsActual = getSimilarProductsUseCase.getSimilarProducts(productId);

        int expectedSize = 3;
        assertEquals(expectedSize, similarProductsActual.size());

    }

    @Test
    void should_returnEmptySet_when_noSimilarProductsFound() {
        String productId = "1";

        when(externalProductServicePort
                .getSimilarProductsIds(anyString()))
                .thenReturn(List.of());

        Set<Product> similarProductsActual = getSimilarProductsUseCase.getSimilarProducts(productId);

        int expectedSize = 0;
        assertEquals(expectedSize, similarProductsActual.size());
    }

    private static List<Product> getProductList() {
        return List.of(
                new Product("2", "Product 2", 20.0, true),
                new Product("3", "Product 3", 30.0, false),
                new Product("4", "Product 4", 40.0, true)
        );
    }
}