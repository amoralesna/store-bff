package com.company.store_bff.products.infra.exceptions;

public class ServiceUnavailableException extends ExternalServiceException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
