package com.jumbotail.ecommerce_shipping.controller;


import com.jumbotail.ecommerce_shipping.dto.response.LocationDto;
import com.jumbotail.ecommerce_shipping.dto.response.NearestWarehouseResponse;
import com.jumbotail.ecommerce_shipping.exception.GlobalExceptionHandler;
import com.jumbotail.ecommerce_shipping.exception.ResourceNotFoundException;
import com.jumbotail.ecommerce_shipping.service.WarehouseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WarehouseController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("WarehouseController Integration Tests")
class WarehouseControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean
    WarehouseService warehouseService;

    @Test
    @DisplayName("GET /api/v1/warehouse/nearest → 200 OK")
    void shouldReturn200WhenValidRequest() throws Exception {
        NearestWarehouseResponse response = NearestWarehouseResponse.builder()
                .warehouseId(1L)
                .warehouseCode("BLR_Warehouse")
                .warehouseName("Bangalore Fulfillment Center")
                .warehouseLocation(new LocationDto(12.99999, 37.923273))
                .distanceFromSellerKm(5.4)
                .build();

        when(warehouseService.getNearestWarehouse(1L, 1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/warehouse/nearest")
                        .param("sellerId", "1")
                        .param("productId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.warehouseId").value(1))
                .andExpect(jsonPath("$.warehouseCode").value("BLR_Warehouse"))
                .andExpect(jsonPath("$.warehouseLocation.lat").value(12.99999))
                .andExpect(jsonPath("$.distanceFromSellerKm").value(5.4));
    }

    @Test
    @DisplayName("GET /api/v1/warehouse/nearest → 404 when seller not found")
    void shouldReturn404WhenSellerNotFound() throws Exception {
        when(warehouseService.getNearestWarehouse(99L, 1L))
                .thenThrow(new ResourceNotFoundException("Seller", 99L));

        mockMvc.perform(get("/api/v1/warehouse/nearest")
                        .param("sellerId", "99")
                        .param("productId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    @DisplayName("GET /api/v1/warehouse/nearest → 400 when sellerId missing")
    void shouldReturn400WhenSellerIdMissing() throws Exception {
        mockMvc.perform(get("/api/v1/warehouse/nearest")
                        .param("productId", "1"))
                .andExpect(status().isBadRequest());
    }
}