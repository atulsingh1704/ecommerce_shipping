package com.jumbotail.ecommerce_shipping.service;


import com.jumbotail.ecommerce_shipping.dto.response.NearestWarehouseResponse;

/**
 * Service interface for warehouse-related business operations.
 */
public interface WarehouseService {

    /**
     * Returns the nearest operational warehouse for a given seller and product.
     *
     * @param sellerId  ID of the seller
     * @param productId ID of the product being shipped
     * @return NearestWarehouseResponse with warehouse details and distance
     */
    NearestWarehouseResponse getNearestWarehouse(Long sellerId, Long productId);
}