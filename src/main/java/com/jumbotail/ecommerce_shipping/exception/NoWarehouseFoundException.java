package com.jumbotail.ecommerce_shipping.exception;


/**
 * Thrown when no operational warehouse exists in the system,
 * or no warehouse can service the requested route.
 */
public class NoWarehouseFoundException extends RuntimeException {

    public NoWarehouseFoundException(String message) {
        super(message);
    }
}