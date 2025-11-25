package com.company.store_bff.shared.infra.exceptions;

public class ServiceUnavailableException extends ExternalServiceException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
