package com.company.store_bff.shared.infra.config;

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
        String productId = "integration-test-123";
        Product product = new Product("similar-1", "Similar Product", 50.0, true);

        productDetailsCache.put(productId, product);

        // Then - Retrieve from cache
        Product cachedProduct = productDetailsCache.getIfPresent(productId);
        assertThat(cachedProduct).isNotNull();
        assertThat(cachedProduct.getId()).isEqualTo("similar-1");
        assertThat(cachedProduct.getName()).isEqualTo("Similar Product");
    }

    @Test
    void productDetailsCache_ReturnsNull_WhenKeyNotPresent() {
        // Given
        String nonExistentKey = "non-existent-key-999";

        // When
        Product result = productDetailsCache.getIfPresent(nonExistentKey);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void productDetailsCache_RecordsStats() {
        // Given
        productDetailsCache.invalidateAll(); // Clear cache
        String key = "stats-test";
        Product value = new Product("1", "Product 1", 10.0, true);

        // When
        productDetailsCache.getIfPresent(key); // MISS
        productDetailsCache.put(key, value);
        productDetailsCache.getIfPresent(key); // HIT
        productDetailsCache.getIfPresent(key); // HIT

        // Then
        assertThat(productDetailsCache.stats().missCount()).isGreaterThanOrEqualTo(1);
        assertThat(productDetailsCache.stats().hitCount()).isGreaterThanOrEqualTo(2);
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

