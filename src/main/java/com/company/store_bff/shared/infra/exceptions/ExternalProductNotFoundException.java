package com.company.store_bff.shared.infra.exceptions;

public class ExternalProductNotFoundException extends NotFoundException {
    public ExternalProductNotFoundException(String message) {
        super(message);
    }
}
