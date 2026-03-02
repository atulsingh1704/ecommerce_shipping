package com.jumbotail.ecommerce_shipping.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * Response for GET /api/v1/shipping-charge
 */
@Data
@Builder
public class ShippingChargeResponse {

    /** Final total shipping charge in INR */
    private double shippingCharge;

    /** Distance from warehouse to customer in km */
    private double distanceKm;

    /** Transport mode used (MINI_VAN / TRUCK / AEROPLANE) */
    private String transportMode;

    /** Delivery speed used (standard / express) */
    private String deliverySpeed;

    /** Warehouse used as origin */
    private Long warehouseId;

    /** Customer destination ID */
    private Long customerId;
}