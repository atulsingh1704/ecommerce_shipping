package com.jumbotail.ecommerce_shipping.dto.response;


import lombok.Builder;
import lombok.Data;

/**
 * Response for POST /api/v1/shipping-charge/calculate
 * Combines nearest warehouse info + final shipping charge.
 */
@Data
@Builder
public class ShippingCalculateResponse {

    /** Final total shipping charge in INR */
    private double shippingCharge;

    /** Details of the nearest warehouse used */
    private NearestWarehouseResponse nearestWarehouse;

    /** Distance from warehouse to customer in km */
    private double distanceKm;

    /** Transport mode used (MINI_VAN / TRUCK / AEROPLANE) */
    private String transportMode;

    /** Delivery speed (standard / express) */
    private String deliverySpeed;

    /** Chargeable weight of the product in kg */
    private double chargeableWeightKg;

    /** Name of the product being shipped */
    private String productName;
}