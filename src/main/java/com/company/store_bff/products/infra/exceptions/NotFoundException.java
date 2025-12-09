package com.company.store_bff.products.infra.exceptions;

public class NotFoundException extends ExternalServiceException {
    public NotFoundException(String message) {
        super(message);
    }
}
