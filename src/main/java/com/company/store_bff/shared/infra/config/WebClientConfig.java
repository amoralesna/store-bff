package com.company.store_bff.shared.infra.config;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public HttpClient httpClient(AppConfigEnvironment appConfigEnvironment) {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, appConfigEnvironment.getConnectionTimeout())
                .responseTimeout(Duration.ofMillis(appConfigEnvironment.getResponseTimeout()));
    }

    @Bean
    public WebClient webClient(HttpClient httpClient, AppConfigEnvironment appConfigEnvironment) {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
