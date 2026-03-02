package com.jumbotail.ecommerce_shipping.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Delivery speed options available to customers.
 *
 * - STANDARD : Rs 10 flat + calculated shipping
 * - EXPRESS  : Rs 10 flat + Rs 1.2/kg + calculated shipping
 */
public enum DeliverySpeed {

    STANDARD("standard"),
    EXPRESS("express");

    private final String value;

    DeliverySpeed(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Case-insensitive deserialization from request params / JSON body.
     * Throws IllegalArgumentException for unsupported values.
     */
    @JsonCreator
    public static DeliverySpeed fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Delivery speed cannot be null");
        }
        for (DeliverySpeed speed : values()) {
            if (speed.value.equalsIgnoreCase(value.trim())) {
                return speed;
            }
        }
        throw new IllegalArgumentException(
                "Unsupported delivery speed: '" + value + "'. Allowed: standard, express"
        );
    }
}