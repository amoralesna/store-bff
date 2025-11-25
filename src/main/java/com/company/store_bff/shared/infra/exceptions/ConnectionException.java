package com.company.store_bff.shared.infra.exceptions;

public class ConnectionException extends ExternalServiceException {
    public ConnectionException(String message) {
        super(message);
    }
}
