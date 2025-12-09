package com.company.store_bff.products.infra.exceptions.handler;

import com.company.store_bff.products.infra.exceptions.NotFoundException;
import com.company.store_bff.products.infra.exceptions.ServiceUnavailableException;
import com.company.store_bff.products.infra.exceptions.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiExceptionHandlerTest {

    private ApiExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ApiExceptionHandler();
    }

    @Test
    void should_return_not_found_status_and_message_when_not_found_exception() {
        NotFoundException ex = new NotFoundException("Resource not found");

        ResponseEntity<String> response = handler.handleNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource not found", response.getBody());
    }

    @Test
    void should_return_request_timeout_status_and_message_when_timeout_exception() {
        TimeoutException ex = new TimeoutException("Request timed out");

        ResponseEntity<String> response = handler.handleTimeoutException(ex);

        assertEquals(HttpStatus.REQUEST_TIMEOUT, response.getStatusCode());
        assertEquals("Request timed out", response.getBody());
    }

    @Test
    void should_return_service_unavailable_status_and_message_when_service_unavailable_exception() {
        ServiceUnavailableException ex = new ServiceUnavailableException("Service unavailable");

        ResponseEntity<String> response = handler.handleServiceUnavailable(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Service unavailable", response.getBody());
    }

    @Test
    void should_return_internal_server_error_with_generic_message_when_external_internal_server_error() {
        InternalServerError ex = mock(InternalServerError.class);
        when(ex.getMessage()).thenReturn("External service failed");

        ResponseEntity<String> response = handler.handleInternalServerError(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred in the external service.", response.getBody());
    }

    @Test
    void should_handle_not_found_with_empty_message_returning_empty_body() {
        NotFoundException ex = new NotFoundException("");

        ResponseEntity<String> response = handler.handleNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("", response.getBody());
    }
}