package com.company.store_bff.products.infra.exceptions;

public class TimeoutException extends ExternalServiceException {
    public TimeoutException(String message) {
        super(message);
    }
}
