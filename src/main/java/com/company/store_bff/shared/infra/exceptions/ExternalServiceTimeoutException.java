package com.company.store_bff.shared.infra.exceptions;

public class ExternalServiceTimeoutException extends RuntimeException {
    public ExternalServiceTimeoutException(String message) {
        super(message);
    }
}
