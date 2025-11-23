package com.company.store_bff.shared.infra.config;

import com.company.store_bff.shared.infra.exceptions.*;
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
                                new ExternalServiceUnavailableException("External service unavailable: " + body)
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
        if (ex instanceof ConnectTimeoutException) {
            return new ExternalServiceTimeoutException("Timeout connecting to external service: " + ex.getMessage());
        }

        if (ex instanceof ReadTimeoutException) {
            return new ExternalServiceTimeoutException("Timeout reading response: " + ex.getMessage());
        }

        if (ex instanceof SocketTimeoutException) {
            return new ExternalServiceTimeoutException("Timeout calling external service: " + ex.getMessage());
        }

        if (ex instanceof PrematureCloseException) {
            return new ExternalProductNotFoundException("Connection unexpectedly closed: " + ex.getMessage());
        }

        if (ex instanceof IOException) {
            return new RuntimeException("Network failure: " + ex.getMessage());
        }

        if (ex instanceof WebClientException) {
            return new RuntimeException("External service error: " + ex.getMessage(), ex);
        }

        return ex;
    }
}