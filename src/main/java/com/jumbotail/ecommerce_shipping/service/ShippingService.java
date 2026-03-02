package com.jumbotail.ecommerce_shipping.service;


import com.jumbotail.ecommerce_shipping.dto.request.ShippingCalculateRequest;
import com.jumbotail.ecommerce_shipping.dto.response.ShippingCalculateResponse;
import com.jumbotail.ecommerce_shipping.dto.response.ShippingChargeResponse;
import com.jumbotail.ecommerce_shipping.enums.DeliverySpeed;

/**
 * Service interface for shipping charge calculations.
 */
public interface ShippingService {

    /**
     * Calculates shipping charge from a warehouse to a customer.
     *
     * @param warehouseId   ID of the source warehouse
     * @param customerId    ID of the destination customer
     * @param deliverySpeed chosen delivery speed
     * @return ShippingChargeResponse with the calculated charge
     */
    ShippingChargeResponse getShippingCharge(
            Long warehouseId, Long customerId, DeliverySpeed deliverySpeed
    );

    /**
     * Full end-to-end shipping calculation: nearest warehouse + shipping charge.
     *
     * @param request contains sellerId, productId, customerId, deliverySpeed
     * @return ShippingCalculateResponse with charge and warehouse details
     */
    ShippingCalculateResponse calculateShipping(ShippingCalculateRequest request);
}