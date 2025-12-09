package com.company.store_bff.products.infra.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@AllArgsConstructor
public class WebClientCustomConfig implements WebClientCustomizer {

    private final WebClientErrorHandler errorHandler;

    @Override
    public void customize(WebClient.Builder builder) {
        builder.filter(errorHandler.errorHandlingFilter());
        builder.filter(errorHandler.networkErrorFilter());
    }
}
