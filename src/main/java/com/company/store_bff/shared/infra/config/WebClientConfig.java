package com.company.store_bff.shared.infra.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
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
                .maxConnections(250)
                .pendingAcquireTimeout(Duration.ofMillis(5000))
                .pendingAcquireMaxCount(300)
                .maxIdleTime(Duration.ofMillis(5000))
                .build();
        return HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, appConfigEnvironment.getConnectionTimeout())
                .responseTimeout(Duration.ofMillis(appConfigEnvironment.getResponseTimeout()));
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder,
                               HttpClient httpClient,
                               CircuitBreakerRegistry circuitBreakerRegistry) {
        return builder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(circuitBreakerFilter(circuitBreakerRegistry))
                .build();
    }

    private ExchangeFilterFunction circuitBreakerFilter(CircuitBreakerRegistry circuitBreakerRegistry) {
        var circuitBreaker = circuitBreakerRegistry.circuitBreaker("externalServices");

        circuitBreaker.getEventPublisher()
                .onStateTransition(event -> log.warn("Circuit Breaker changed state: {}", event))
                .onError(event -> log.error("Circuit Breaker error: {}", event.getThrowable().getMessage(), event.getThrowable()));

        return (request, next) -> next.exchange(request)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }
}
