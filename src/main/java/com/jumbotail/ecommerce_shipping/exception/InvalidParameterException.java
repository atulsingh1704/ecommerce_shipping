package com.jumbotail.ecommerce_shipping.exception;


/**
 * Thrown when a request parameter is missing, null, or contains an invalid value.
 */
public class InvalidParameterException extends RuntimeException {

    public InvalidParameterException(String message) {
        super(message);
    }
}