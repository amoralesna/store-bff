package com.company.store_bff.products.infra.config;

import com.company.store_bff.products.infra.exceptions.ConnectionException;
import com.company.store_bff.products.infra.exceptions.NotFoundException;
import com.company.store_bff.products.infra.exceptions.ServiceUnavailableException;
import com.company.store_bff.products.infra.exceptions.TimeoutException;
import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.ReadTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.PrematureCloseException;

import java.io.IOException;
import java.net.SocketTimeoutException;

@Component
public class WebClientErrorHandler {

    public ExchangeFilterFunction errorHandlingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {

            if (clientResponse.statusCode().value() == HttpStatus.NOT_FOUND.value()) {
                return clientResponse
                        .bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new NotFoundException(body)));
            }

            if (clientResponse.statusCode().value() == HttpStatus.SERVICE_UNAVAILABLE.value()) {
                return clientResponse
                        .bodyToMono(String.class)
                        .flatMap(body -> Mono.error(
                                new ServiceUnavailableException("External service unavailable: " + body)
                        ));
            }

            return Mono.just(clientResponse);
        });
    }

    public ExchangeFilterFunction networkErrorFilter() {
        return (request, next) ->
                next.exchange(request).onErrorMap(this::mapException);
    }

    private Throwable mapException(Throwable ex) {
        if (ex instanceof WebClientException) {
            if (ex.getCause() instanceof ConnectTimeoutException) {
                return new TimeoutException("Timeout connecting to external service: " + ex.getMessage());
            }

            if (ex.getCause() instanceof ReadTimeoutException) {
                return new TimeoutException("Timeout reading response: " + ex.getMessage());
            }

            if (ex.getCause() instanceof SocketTimeoutException) {
                return new TimeoutException("Timeout calling external service: " + ex.getMessage());
            }

            if (ex.getCause() instanceof PrematureCloseException) {
                return new ConnectionException("Connection unexpectedly closed: " + ex.getMessage());
            }

            if (ex.getCause() instanceof IOException) {
                return new ConnectionException("Network failure: " + ex.getMessage());
            }

            return new RuntimeException("External service error: " + ex.getMessage(), ex);
        }

        return ex;
    }
}