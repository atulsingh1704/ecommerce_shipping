package com.jumbotail.ecommerce_shipping.exception;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response body returned for all API errors.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /** HTTP status code (e.g., 400, 404, 500) */
    private int status;

    /** Short error type (e.g., "NOT_FOUND", "BAD_REQUEST") */
    private String error;

    /** Human-readable message describing what went wrong */
    private String message;

    /** The API path that triggered the error */
    private String path;

    /** Timestamp of when the error occurred */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /** Field-level validation errors (populated only for 400 errors) */
    private Map<String, String> fieldErrors;
}