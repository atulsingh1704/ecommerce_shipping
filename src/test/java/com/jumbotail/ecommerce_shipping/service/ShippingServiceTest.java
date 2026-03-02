package com.jumbotail.ecommerce_shipping.service;


import com.jumbotail.ecommerce_shipping.dto.request.ShippingCalculateRequest;
import com.jumbotail.ecommerce_shipping.dto.response.*;
import com.jumbotail.ecommerce_shipping.entity.*;
import com.jumbotail.ecommerce_shipping.enums.DeliverySpeed;
import com.jumbotail.ecommerce_shipping.exception.InvalidParameterException;
import com.jumbotail.ecommerce_shipping.exception.ResourceNotFoundException;
import com.jumbotail.ecommerce_shipping.repository.*;
import com.jumbotail.ecommerce_shipping.service.impl.ShippingServiceImpl;
import com.jumbotail.ecommerce_shipping.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShippingService Unit Tests")
class ShippingServiceTest {

    @Mock private WarehouseRepository       warehouseRepository;
    @Mock private CustomerRepository        customerRepository;
    @Mock private ProductRepository         productRepository;
    @Mock private SellerRepository          sellerRepository;
    @Mock private DistanceCalculatorService distanceCalculatorService;
    @Mock private ShippingStrategyFactory   strategyFactory;
    @Mock private WarehouseService          warehouseService;

    @InjectMocks
    private ShippingServiceImpl shippingService;

    private Warehouse warehouse;
    private Customer  customer;
    private Product   product;
    private Seller    seller;

    @BeforeEach
    void setUp() {
        warehouse = Warehouse.builder()
                .id(1L).warehouseCode("BLR_Warehouse").operational(true)
                .location(new Location(12.99999, 37.923273))
                .build();

        customer = Customer.builder()
                .id(1L).storeName("Shree Kirana").active(true)
                .location(new Location(11.232, 23.445495))
                .build();

        seller = Seller.builder()
                .id(1L).sellerName("Nestle").active(true)
                .location(new Location(12.9716, 77.5946))
                .build();

        product = Product.builder()
                .id(1L).productName("Maggie 500g").active(true)
                .weightKg(0.5).lengthCm(10).widthCm(10).heightCm(10)
                .seller(seller)
                .build();
    }

    @Test
    @DisplayName("Should calculate standard shipping charge correctly")
    void shouldCalculateStandardShipping() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(distanceCalculatorService.calculateDistance(any(), any())).thenReturn(50.0);
        // 50 km → MINI_VAN @ 3 Rs/km/kg × 1 kg = 150 base
        // Standard: 10 + 150 = 160
        StandardShippingStrategy strategy = new StandardShippingStrategy();
        when(strategyFactory.getStrategy(DeliverySpeed.STANDARD)).thenReturn(strategy);

        var response = shippingService.getShippingCharge(1L, 1L, DeliverySpeed.STANDARD);

        assertThat(response.getShippingCharge()).isEqualTo(160.0);
        assertThat(response.getTransportMode()).isEqualTo("MINI_VAN");
    }

    @Test
    @DisplayName("Should calculate express shipping charge with surcharge")
    void shouldCalculateExpressShipping() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(distanceCalculatorService.calculateDistance(any(), any())).thenReturn(50.0);
        // 50km × 1kg × 3 Rs = 150 base
        // Express: 10 + (1.2 × 1) + 150 = 161.2
        ExpressShippingStrategy strategy = new ExpressShippingStrategy();
        when(strategyFactory.getStrategy(DeliverySpeed.EXPRESS)).thenReturn(strategy);

        var response = shippingService.getShippingCharge(1L, 1L, DeliverySpeed.EXPRESS);

        assertThat(response.getShippingCharge()).isEqualTo(161.2);
        assertThat(response.getDeliverySpeed()).isEqualTo("express");
    }

    @Test
    @DisplayName("Should use TRUCK for 200km distance")
    void shouldUseTruckFor200km() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(distanceCalculatorService.calculateDistance(any(), any())).thenReturn(200.0);
        // 200km → TRUCK @ 2 Rs/km/kg × 1kg = 400 base
        StandardShippingStrategy strategy = new StandardShippingStrategy();
        when(strategyFactory.getStrategy(DeliverySpeed.STANDARD)).thenReturn(strategy);

        var response = shippingService.getShippingCharge(1L, 1L, DeliverySpeed.STANDARD);

        assertThat(response.getTransportMode()).isEqualTo("TRUCK");
        assertThat(response.getShippingCharge()).isEqualTo(410.0); // 10 + 400
    }

    @Test
    @DisplayName("Should use AEROPLANE for 600km distance")
    void shouldUseAeroplaneFor600km() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(distanceCalculatorService.calculateDistance(any(), any())).thenReturn(600.0);
        StandardShippingStrategy strategy = new StandardShippingStrategy();
        when(strategyFactory.getStrategy(DeliverySpeed.STANDARD)).thenReturn(strategy);

        var response = shippingService.getShippingCharge(1L, 1L, DeliverySpeed.STANDARD);

        assertThat(response.getTransportMode()).isEqualTo("AEROPLANE");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when warehouse not found")
    void shouldThrowWhenWarehouseNotFound() {
        when(warehouseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                shippingService.getShippingCharge(99L, 1L, DeliverySpeed.STANDARD)
        ).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw when inactive warehouse requested")
    void shouldThrowForInactiveWarehouse() {
        warehouse.setOperational(false);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        assertThatThrownBy(() ->
                shippingService.getShippingCharge(1L, 1L, DeliverySpeed.STANDARD)
        ).isInstanceOf(InvalidParameterException.class)
                .hasMessageContaining("not currently operational");
    }

    @Test
    @DisplayName("Full calculate: should combine nearest warehouse + product weight correctly")
    void shouldCalculateFullShipping() {
        ShippingCalculateRequest req = new ShippingCalculateRequest();
        req.setSellerId(1L);
        req.setProductId(1L);
        req.setCustomerId(1L);
        req.setDeliverySpeed(DeliverySpeed.STANDARD);

        NearestWarehouseResponse nearestWh = NearestWarehouseResponse.builder()
                .warehouseId(1L).warehouseCode("BLR_Warehouse")
                .warehouseLocation(new LocationDto(12.99999, 37.923273))
                .distanceFromSellerKm(100.0)
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseService.getNearestWarehouse(1L, 1L)).thenReturn(nearestWh);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(distanceCalculatorService.calculateDistance(any(), any())).thenReturn(300.0);
        // 300km → TRUCK @ 2 Rs/km/kg × 0.5kg = 300 base
        // Standard: 10 + 300 = 310
        when(strategyFactory.getStrategy(DeliverySpeed.STANDARD))
                .thenReturn(new StandardShippingStrategy());

        var response = shippingService.calculateShipping(req);

        assertThat(response.getShippingCharge()).isEqualTo(310.0);
        assertThat(response.getTransportMode()).isEqualTo("TRUCK");
        assertThat(response.getChargeableWeightKg()).isEqualTo(0.5);
        assertThat(response.getProductName()).isEqualTo("Maggie 500g");
    }
}