package com.company.store_bff.products.infra.exceptions;

public class ConnectionException extends ExternalServiceException {
    public ConnectionException(String message) {
        super(message);
    }
}
