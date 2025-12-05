package com.company.store_bff.shared.infra.config;

import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
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
    public WebClient webClient(WebClient.Builder builder,
                               HttpClient httpClient,
                               CircuitBreakerRegistry circuitBreakerRegistry,
                               BulkheadRegistry bulkheadRegistry) {
        return builder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(bulkheadFilter(bulkheadRegistry))
                .filter(circuitBreakerFilter(circuitBreakerRegistry))
                .build();
    }

    private ExchangeFilterFunction bulkheadFilter(BulkheadRegistry bulkheadRegistry) {
        var bulkhead = bulkheadRegistry.bulkhead("externalServices");

        bulkhead.getEventPublisher()
                .onCallPermitted(event -> log.debug("Bulkhead call permitted"))
                .onCallRejected(event -> log.warn("Bulkhead call REJECTED - Max concurrent calls reached"))
                .onCallFinished(event -> log.debug("Bulkhead call finished"));

        return (request, next) -> next.exchange(request)
                .transformDeferred(BulkheadOperator.of(bulkhead));
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
