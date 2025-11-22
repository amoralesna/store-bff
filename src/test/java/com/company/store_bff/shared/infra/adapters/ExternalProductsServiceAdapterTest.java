package com.company.store_bff.shared.infra.adapters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.MockitoAnnotations.openMocks;

class ExternalProductsServiceAdapterTest {

    private ExternalProductsServiceAdapter externalProductsServiceAdapter;

    @BeforeEach
    void setUp() {
        externalProductsServiceAdapter = new ExternalProductsServiceAdapter();
    }

    @Test
    void should_getSimilarProductsIds() {
        assertEquals(0, externalProductsServiceAdapter.getSimilarProductsIds(anyString()).size());
    }

    @Test
    void should_getProductsDetail() {
        assertEquals(0, externalProductsServiceAdapter.getProductsDetail(anyList()).size());
    }
}