package com.app.asaas.demo.shared.dto;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;

public record ErrorResponseDTO(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors) {

    public static ErrorResponseDTO of(HttpStatus status, String message, String path) {
        return of(status, message, path, Map.of());
    }

    public static ErrorResponseDTO of(HttpStatus status, String message, String path, Map<String, String> fieldErrors) {
        return new ErrorResponseDTO(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                fieldErrors);
    }

}
