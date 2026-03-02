package com.jumbotail.ecommerce_shipping.service;

import com.jumbotail.ecommerce_shipping.dto.response.NearestWarehouseResponse;
import com.jumbotail.ecommerce_shipping.entity.*;
import com.jumbotail.ecommerce_shipping.exception.InvalidParameterException;
import com.jumbotail.ecommerce_shipping.exception.NoWarehouseFoundException;
import com.jumbotail.ecommerce_shipping.exception.ResourceNotFoundException;
import com.jumbotail.ecommerce_shipping.repository.*;
import com.jumbotail.ecommerce_shipping.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WarehouseService Unit Tests")
class WarehouseServiceTest {

    @Mock private SellerRepository     sellerRepository;
    @Mock private ProductRepository    productRepository;
    @Mock private WarehouseRepository  warehouseRepository;
    @Mock private DistanceCalculatorService distanceCalculatorService;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    private Seller seller;
    private Product product;
    private Warehouse blr;
    private Warehouse mumb;

    @BeforeEach
    void setUp() {
        seller = Seller.builder()
                .id(1L).sellerName("Nestle").active(true)
                .location(new Location(12.9716, 77.5946))
                .build();

        product = Product.builder()
                .id(1L).productName("Maggie 500g").active(true).weightKg(0.5)
                .lengthCm(10).widthCm(10).heightCm(10)
                .seller(seller)
                .build();

        blr = Warehouse.builder()
                .id(1L).warehouseCode("BLR_Warehouse").warehouseName("Bangalore Fulfillment Center")
                .location(new Location(12.99999, 37.923273)).operational(true)
                .build();

        mumb = Warehouse.builder()
                .id(2L).warehouseCode("MUMB_Warehouse").warehouseName("Mumbai Fulfillment Center")
                .location(new Location(11.99999, 27.923273)).operational(true)
                .build();
    }

    @Test
    @DisplayName("Should return nearest warehouse for valid seller and product")
    void shouldReturnNearestWarehouse() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findByOperationalTrue()).thenReturn(List.of(blr, mumb));
        when(distanceCalculatorService.findNearestWarehouse(any(), any())).thenReturn(blr);

        NearestWarehouseResponse response = warehouseService.getNearestWarehouse(1L, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getWarehouseCode()).isEqualTo("BLR_Warehouse");
        assertThat(response.getWarehouseId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when seller not found")
    void shouldThrowWhenSellerNotFound() {
        when(sellerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> warehouseService.getNearestWarehouse(99L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product not found")
    void shouldThrowWhenProductNotFound() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> warehouseService.getNearestWarehouse(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Should throw InvalidParameterException when product doesn't belong to seller")
    void shouldThrowWhenProductNotBelongsToSeller() {
        Seller otherSeller = Seller.builder().id(2L).sellerName("Other").active(true)
                .location(new Location(13.0, 77.6)).build();
        Product otherProduct = Product.builder().id(2L).active(true)
                .seller(otherSeller).build();

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(productRepository.findById(2L)).thenReturn(Optional.of(otherProduct));

        assertThatThrownBy(() -> warehouseService.getNearestWarehouse(1L, 2L))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessageContaining("does not belong");
    }

    @Test
    @DisplayName("Should throw NoWarehouseFoundException when no operational warehouses exist")
    void shouldThrowWhenNoWarehousesAvailable() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findByOperationalTrue()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> warehouseService.getNearestWarehouse(1L, 1L))
                .isInstanceOf(NoWarehouseFoundException.class)
                .hasMessageContaining("No operational warehouses");
    }

    @Test
    @DisplayName("Should throw InvalidParameterException for non-positive sellerId")
    void shouldThrowForInvalidSellerId() {
        assertThatThrownBy(() -> warehouseService.getNearestWarehouse(-1L, 1L))
                .isInstanceOf(InvalidParameterException.class);
    }

    @Test
    @DisplayName("Should throw InvalidParameterException for inactive seller")
    void shouldThrowForInactiveSeller() {
        seller.setActive(false);
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));

        assertThatThrownBy(() -> warehouseService.getNearestWarehouse(1L, 1L))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessageContaining("not active");
    }
}