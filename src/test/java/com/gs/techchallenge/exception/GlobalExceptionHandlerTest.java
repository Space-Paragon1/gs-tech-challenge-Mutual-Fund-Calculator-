package com.gs.techchallenge.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleResponseStatusReturnsCorrectStatusAndMessage() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");

        ResponseEntity<Map<String, Object>> response = handler.handleResponseStatus(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("status", 404);
        assertThat(response.getBody()).containsEntry("error", "Resource not found");
        assertThat(response.getBody()).containsKey("timestamp");
    }

    @Test
    void handleValidationErrorsReturnsBadRequest() {
        ResponseEntity<Map<String, Object>> response = handler.handleValidationErrors(new RuntimeException("bad input"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("status", 400);
        assertThat(response.getBody()).containsEntry("error", "Invalid request parameters");
        assertThat(response.getBody()).containsKey("timestamp");
    }

    @Test
    void handleExternalApiExceptionReturnsBadGateway() {
        ExternalApiException ex = new ExternalApiException("Upstream service unavailable");

        ResponseEntity<Map<String, Object>> response = handler.handleExternalApiException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).containsEntry("status", 502);
        assertThat(response.getBody()).containsEntry("error", "Upstream service unavailable");
        assertThat(response.getBody()).containsKey("timestamp");
    }

    @Test
    void handleGenericExceptionReturnsInternalServerError() {
        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(new RuntimeException("unexpected"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("status", 500);
        assertThat(response.getBody()).containsEntry("error", "Unexpected server error");
        assertThat(response.getBody()).containsKey("timestamp");
    }

    @Test
    void responseBodyAlwaysContainsTimestampStatusAndError() {
        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(new RuntimeException());

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKeys("timestamp", "status", "error");
    }

    @Test
    void handleResponseStatusWithConflictStatus() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.CONFLICT, "Already exists");

        ResponseEntity<Map<String, Object>> response = handler.handleResponseStatus(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("status", 409);
        assertThat(response.getBody()).containsEntry("error", "Already exists");
    }
}
