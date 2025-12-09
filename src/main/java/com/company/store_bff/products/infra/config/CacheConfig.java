package com.company.store_bff.products.infra.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.company.store_bff.products.domain.models.Product;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
@Slf4j
public class CacheConfig {

    private final AppConfigEnvironment appConfigEnvironment;

    @Bean
    public Cache<String, Product> productDetailsCache() {
        log.info("Initializing productDetailsCache with maximumSize={}, expireAfterWrite={}",
                appConfigEnvironment.getCacheMaximumSize(),
                appConfigEnvironment.getCacheExpireAfterWrite());

        return Caffeine.newBuilder()
                .maximumSize(appConfigEnvironment.getCacheMaximumSize())
                .expireAfterWrite(appConfigEnvironment.getCacheExpireAfterWrite())
                .recordStats()
                .build();
    }
}

