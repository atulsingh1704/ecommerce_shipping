package com.jumbotail.ecommerce_shipping.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumbotail.ecommerce_shipping.dto.request.ShippingCalculateRequest;
import com.jumbotail.ecommerce_shipping.dto.response.*;
import com.jumbotail.ecommerce_shipping.enums.DeliverySpeed;
import com.jumbotail.ecommerce_shipping.exception.GlobalExceptionHandler;
import com.jumbotail.ecommerce_shipping.service.ShippingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShippingController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("ShippingController Integration Tests")

class ShippingControllerTest {

    @Autowired
    MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean
    ShippingService shippingService;

    @Test
    @DisplayName("GET /api/v1/shipping-charge → 200 OK with standard speed")
    void shouldReturnStandardShippingCharge() throws Exception {
        ShippingChargeResponse response = ShippingChargeResponse.builder()
                .shippingCharge(160.0)
                .distanceKm(50.0)
                .transportMode("MINI_VAN")
                .deliverySpeed("standard")
                .warehouseId(1L)
                .customerId(1L)
                .build();

        when(shippingService.getShippingCharge(any(), any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/shipping-charge")
                .param("warehouseId", "1")
                .param("customerId", "1")
                .param("deliverySpeed", "standard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shippingCharge").value(160.0))
                .andExpect(jsonPath("$.transportMode").value("MINI_VAN"));
    }

    @Test
    @DisplayName("POST /api/v1/shipping-charge/calculate → 200 OK")
    void shouldCalculateShipping() throws Exception {
        ShippingCalculateRequest request = new ShippingCalculateRequest();
        request.setSellerId(1L);
        request.setProductId(1L);
        request.setCustomerId(1L);
        request.setDeliverySpeed(DeliverySpeed.EXPRESS);

        ShippingCalculateResponse response = ShippingCalculateResponse.builder()
                .shippingCharge(180.0)
                .nearestWarehouse(NearestWarehouseResponse.builder()
                        .warehouseId(1L)
                        .warehouseLocation(new LocationDto(12.99999, 37.923273))
                        .build())
                .distanceKm(100.0)
                .transportMode("TRUCK")
                .deliverySpeed("express")
                .chargeableWeightKg(0.5)
                .productName("Maggie 500g")
                .build();

        when(shippingService.calculateShipping(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/shipping-charge/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shippingCharge").value(180.0))
                .andExpect(jsonPath("$.nearestWarehouse.warehouseId").value(1))
                .andExpect(jsonPath("$.transportMode").value("TRUCK"));
    }

    @Test
    @DisplayName("POST /api/v1/shipping-charge/calculate → 400 when sellerId missing")
    void shouldReturn400WhenRequestBodyInvalid() throws Exception {
        // Missing sellerId
        String invalidBody = """
                {
                    "customerId": 1,
                    "productId": 1,
                    "deliverySpeed": "standard"
                }
                """;

        mockMvc.perform(post("/api/v1/shipping-charge/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.sellerId").exists());
    }

    @Test
    @DisplayName("GET /api/v1/shipping-charge → 400 for invalid delivery speed")
    void shouldReturn400ForInvalidDeliverySpeed() throws Exception {
        mockMvc.perform(get("/api/v1/shipping-charge")
                .param("warehouseId", "1")
                .param("customerId", "1")
                .param("deliverySpeed", "superfast"))
                .andExpect(status().isBadRequest());
    }
}
