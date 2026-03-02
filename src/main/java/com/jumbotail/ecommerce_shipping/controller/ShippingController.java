package com.jumbotail.ecommerce_shipping.controller;


import com.jumbotail.ecommerce_shipping.dto.request.ShippingCalculateRequest;
import com.jumbotail.ecommerce_shipping.dto.response.ShippingCalculateResponse;
import com.jumbotail.ecommerce_shipping.dto.response.ShippingChargeResponse;
import com.jumbotail.ecommerce_shipping.enums.DeliverySpeed;
import com.jumbotail.ecommerce_shipping.service.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for shipping charge operations.
 *
 * Base path: /api/v1/shipping-charge
 */
@RestController
@RequestMapping("/api/v1/shipping-charge")
@Validated
@Tag(name = "Shipping Charge API", description = "Calculate shipping charges for orders")
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    /**
     * GET /api/v1/shipping-charge?warehouseId=1&customerId=2&deliverySpeed=standard
     *
     * Calculates the shipping charge from a specific warehouse to a customer
     * using a default weight of 1 kg (use /calculate for product-specific costing).
     */
    @GetMapping
    @Operation(
            summary     = "Get shipping charge from warehouse to customer",
            description = "Calculates shipping charge based on distance and delivery speed. "
                    + "Uses 1 kg as default chargeable weight. "
                    + "For product-specific charges, use POST /calculate."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shipping charge calculated"),
            @ApiResponse(responseCode = "400", description = "Invalid or missing parameters"),
            @ApiResponse(responseCode = "404", description = "Warehouse or Customer not found")
    })
    public ResponseEntity<ShippingChargeResponse> getShippingCharge(
            @Parameter(description = "Warehouse ID", required = true, example = "1")
            @RequestParam @Positive(message = "warehouseId must be positive") Long warehouseId,

            @Parameter(description = "Customer ID", required = true, example = "1")
            @RequestParam @Positive(message = "customerId must be positive") Long customerId,

            @Parameter(description = "Delivery speed: standard or express",
                    required = true, example = "standard")
            @RequestParam String deliverySpeed) {

        DeliverySpeed speed = DeliverySpeed.fromValue(deliverySpeed);
        ShippingChargeResponse response =
                shippingService.getShippingCharge(warehouseId, customerId, speed);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/shipping-charge/calculate
     *
     * Full end-to-end calculation: finds the nearest warehouse for the seller's product,
     * then calculates the shipping charge to the customer using actual product weight.
     */
    @PostMapping("/calculate")
    @Operation(
            summary     = "Full shipping charge calculation",
            description = "Finds the nearest warehouse for the seller + product, "
                    + "then calculates exact shipping charge to customer "
                    + "using actual/volumetric product weight."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shipping charge calculated"),
            @ApiResponse(responseCode = "400", description = "Validation error or invalid values"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    public ResponseEntity<ShippingCalculateResponse> calculateShipping(
            @Valid @RequestBody ShippingCalculateRequest request) {

        ShippingCalculateResponse response = shippingService.calculateShipping(request);
        return ResponseEntity.ok(response);
    }
}