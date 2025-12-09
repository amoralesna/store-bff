package com.company.store_bff.products.infra.config;

import com.company.store_bff.products.domain.models.Product;
import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CacheConfigIntegrationTest {

    @Autowired
    private Cache<String, Product> productDetailsCache;

    @Test
    void productDetailsCache_IsProperlyConfigured() {
        // Given
        assertThat(productDetailsCache).isNotNull();

        // When - Put value in cache
        String productId = "1";
        Product product = new Product("1", "Similar Product", 50.0, true);

        productDetailsCache.put(productId, product);

        // Then - Retrieve from cache
        Product cachedProduct = productDetailsCache.getIfPresent(productId);
        assertThat(cachedProduct).isNotNull();
        assertThat(cachedProduct.getId()).isEqualTo("1");
        assertThat(cachedProduct.getName()).isEqualTo("Similar Product");
    }

    @Test
    void productDetailsCache_ReturnsNull_WhenKeyNotPresent() {
        // Given
        String nonExistentKey = "999";

        // When
        Product result = productDetailsCache.getIfPresent(nonExistentKey);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void productDetailsCache_Invalidate_RemovesEntry() {
        // Given
        String key = "invalidate-test";
        Product value = new Product("2", "Product 2", 20.0, false);
        productDetailsCache.put(key, value);
        assertThat(productDetailsCache.getIfPresent(key)).isNotNull();

        // When
        productDetailsCache.invalidate(key);

        // Then
        assertThat(productDetailsCache.getIfPresent(key)).isNull();
    }
}

