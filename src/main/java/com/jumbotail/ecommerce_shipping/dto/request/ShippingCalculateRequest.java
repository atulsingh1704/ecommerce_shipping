package com.jumbotail.ecommerce_shipping.dto.request;


import com.jumbotail.ecommerce_shipping.enums.DeliverySpeed;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Request body for POST /api/v1/shipping-charge/calculate
 */
@Data
public class ShippingCalculateRequest {

    /** ID of the seller sending the product */
    @NotNull(message = "sellerId is required")
    @Positive(message = "sellerId must be a positive number")
    private Long sellerId;

    /** ID of the product being shipped */
    @NotNull(message = "productId is required")
    @Positive(message = "productId must be a positive number")
    private Long productId;

    /** ID of the customer receiving the product */
    @NotNull(message = "customerId is required")
    @Positive(message = "customerId must be a positive number")
    private Long customerId;

    /**
     * Delivery speed: "standard" or "express"
     * Deserialized via DeliverySpeed.fromValue() for case-insensitive matching.
     */
    @NotNull(message = "deliverySpeed is required")
    private DeliverySpeed deliverySpeed;
}