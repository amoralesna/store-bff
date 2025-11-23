package com.company.store_bff.shared.infra.config;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

}