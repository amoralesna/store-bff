package com.company.store_bff.shared.infra.config;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@NoArgsConstructor
@Component
public class AppConfigEnvironment {

    @Value("${clients.external-products-service.uri.product-similar-ids}")
    private String externalProductsServiceUriProductSimilarIds;

    @Value("${clients.external-products-service.uri.product-detail}")
    private String externalProductsServiceUriProductDetail;

    @Value("${clients.timeout.connection:5000}")
    private int connectionTimeout;

    @Value("${clients.timeout.response:5000}")
    private int responseTimeout;

    @Value("${clients.max-concurrent-requests.product-detail:5}")
    private int maxConcurrentRequestsProductDetail;

    @Value("${cache.similar-products.maximum-size:500}")
    private long cacheMaximumSize;

    @Value("${cache.similar-products.expire-after-write:10m}")
    private Duration cacheExpireAfterWrite;

    @Value("${app.config.webclient.connection-pool.max-connections:250}")
    private int connectionPoolMaxConnections;

    @Value("${app.config.webclient.connection-pool.pending-acquire-timeout:20000}")
    private int connectionPoolPendingAcquireTimeout;

    @Value("${app.config.webclient.connection-pool.pending-acquire-max-count:300}")
    private int connectionPoolPendingAcquireMaxCount;

    @Value("${app.config.webclient.connection-pool.max-idle-time:10000}")
    private int connectionPoolMaxIdleTime;

}