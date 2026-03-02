package com.jumbotail.ecommerce_shipping.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * Response for GET /api/v1/warehouse/nearest
 */
@Data
@Builder
public class NearestWarehouseResponse {

    /** Database ID of the warehouse */
    private Long warehouseId;

    /** Human-readable code (e.g., "BLR_Warehouse") */
    private String warehouseCode;

    /** Display name */
    private String warehouseName;

    /** GPS coordinates of the warehouse */
    private LocationDto warehouseLocation;

    /** Straight-line distance from seller to this warehouse in km */
    private double distanceFromSellerKm;
}