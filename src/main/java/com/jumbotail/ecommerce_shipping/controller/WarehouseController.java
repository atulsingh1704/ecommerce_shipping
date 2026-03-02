package com.jumbotail.ecommerce_shipping.controller;


import com.jumbotail.ecommerce_shipping.dto.response.NearestWarehouseResponse;
import com.jumbotail.ecommerce_shipping.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for warehouse-related operations.
 *
 * Base path: /api/v1/warehouse
 */
@RestController
@RequestMapping("/api/v1/warehouse")
@Validated
@Tag(name = "Warehouse API", description = "Operations related to marketplace warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    /**
     * GET /api/v1/warehouse/nearest?sellerId=123&productId=456
     *
     * Returns the nearest operational warehouse where the seller should drop off
     * the specified product.
     *
     * @param sellerId  the ID of the seller
     * @param productId the ID of the product to be shipped
     * @return nearest warehouse details including location and distance
     */
    @GetMapping("/nearest")
    @Operation(
            summary     = "Get nearest warehouse for a seller + product",
            description = "Returns the nearest operational marketplace warehouse "
                    + "relative to the seller's location."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nearest warehouse found"),
            @ApiResponse(responseCode = "400", description = "Invalid or missing parameters"),
            @ApiResponse(responseCode = "404", description = "Seller, Product, or Warehouse not found")
    })
    public ResponseEntity<NearestWarehouseResponse> getNearestWarehouse(
            @Parameter(description = "Seller ID", required = true, example = "1")
            @RequestParam @Positive(message = "sellerId must be positive") Long sellerId,

            @Parameter(description = "Product ID", required = true, example = "1")
            @RequestParam @Positive(message = "productId must be positive") Long productId) {

        NearestWarehouseResponse response = warehouseService.getNearestWarehouse(sellerId, productId);
        return ResponseEntity.ok(response);
    }
}