package com.company.store_bff.shared.infra.config;

import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
@Slf4j
public class WebClientConfig {

    @Bean
    public HttpClient httpClient(AppConfigEnvironment appConfigEnvironment) {
        ConnectionProvider provider = ConnectionProvider.builder("custom-pool")
                .maxConnections(appConfigEnvironment.getConnectionPoolMaxConnections())
                .pendingAcquireTimeout(Duration.ofMillis(appConfigEnvironment.getConnectionPoolPendingAcquireTimeout()))
                .pendingAcquireMaxCount(appConfigEnvironment.getConnectionPoolPendingAcquireMaxCount())
                .maxIdleTime(Duration.ofMillis(appConfigEnvironment.getConnectionPoolMaxIdleTime()))
                .build();
        return HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, appConfigEnvironment.getConnectionTimeout())
                .responseTimeout(Duration.ofMillis(appConfigEnvironment.getResponseTimeout()));
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder, HttpClient httpClient) {
        return builder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
