package com.jumbotail.ecommerce_shipping.service.impl;

import com.jumbotail.ecommerce_shipping.dto.request.ShippingCalculateRequest;
import com.jumbotail.ecommerce_shipping.dto.response.*;
import com.jumbotail.ecommerce_shipping.entity.Customer;
import com.jumbotail.ecommerce_shipping.entity.Product;
import com.jumbotail.ecommerce_shipping.entity.Warehouse;
import com.jumbotail.ecommerce_shipping.enums.DeliverySpeed;
import com.jumbotail.ecommerce_shipping.enums.TransportMode;
import com.jumbotail.ecommerce_shipping.exception.InvalidParameterException;
import com.jumbotail.ecommerce_shipping.exception.ResourceNotFoundException;
import com.jumbotail.ecommerce_shipping.repository.CustomerRepository;
import com.jumbotail.ecommerce_shipping.repository.ProductRepository;
import com.jumbotail.ecommerce_shipping.repository.SellerRepository;
import com.jumbotail.ecommerce_shipping.repository.WarehouseRepository;
import com.jumbotail.ecommerce_shipping.service.DistanceCalculatorService;
import com.jumbotail.ecommerce_shipping.service.ShippingService;
import com.jumbotail.ecommerce_shipping.service.WarehouseService;
import com.jumbotail.ecommerce_shipping.strategy.ShippingCostStrategy;
import com.jumbotail.ecommerce_shipping.strategy.ShippingStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Implementation of ShippingService.
 *
 * Shipping charge formula:
 * 1. baseCharge = distanceKm × chargeableWeightKg ×
 * transportMode.ratePerKmPerKg
 * 2. totalCharge = strategy.calculate(baseCharge, chargeableWeightKg)
 *
 * Chargeable weight = max(actual weight, volumetric weight)
 * Transport mode = determined by distance (MiniVan / Truck / Aeroplane)
 * Strategy = determined by deliverySpeed (Standard / Express)
 */
@Service
public class ShippingServiceImpl implements ShippingService {

        private static final Logger log = LoggerFactory.getLogger(ShippingServiceImpl.class);

        private final WarehouseRepository warehouseRepository;
        private final CustomerRepository customerRepository;
        private final ProductRepository productRepository;
        private final SellerRepository sellerRepository;
        private final DistanceCalculatorService distanceCalculatorService;
        private final ShippingStrategyFactory strategyFactory;
        private final WarehouseService warehouseService;

        public ShippingServiceImpl(WarehouseRepository warehouseRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository,
                        SellerRepository sellerRepository,
                        DistanceCalculatorService distanceCalculatorService,
                        ShippingStrategyFactory strategyFactory,
                        WarehouseService warehouseService) {
                this.warehouseRepository = warehouseRepository;
                this.customerRepository = customerRepository;
                this.productRepository = productRepository;
                this.sellerRepository = sellerRepository;
                this.distanceCalculatorService = distanceCalculatorService;
                this.strategyFactory = strategyFactory;
                this.warehouseService = warehouseService;
        }

        /**
         * Calculates shipping charge from a given warehouse to a customer.
         * Cached by warehouseId + customerId + deliverySpeed.
         *
         * NOTE: This uses the heaviest product of the seller as a representative
         * weight when called standalone without a productId.
         * For more accurate results, use calculateShipping() which passes productId.
         */
        @Override
        @Cacheable(value = "shippingCharge", key = "'' + #warehouseId + '-' + #customerId + '-' + #deliverySpeed.name()")
        public ShippingChargeResponse getShippingCharge(
                        Long warehouseId, Long customerId, DeliverySpeed deliverySpeed) {

                log.debug("Calculating shipping: warehouse={}, customer={}, speed={}",
                                warehouseId, customerId, deliverySpeed);

                // 1. Validate warehouse
                Warehouse warehouse = warehouseRepository.findById(warehouseId)
                                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", warehouseId));

                if (!warehouse.isOperational()) {
                        throw new InvalidParameterException(
                                        "Warehouse " + warehouseId + " is not currently operational");
                }

                // 2. Validate customer
                Customer customer = customerRepository.findById(customerId)
                                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

                if (!customer.isActive()) {
                        throw new InvalidParameterException("Customer " + customerId + " is not active");
                }

                // 3. Compute distance
                double distanceKm = distanceCalculatorService.calculateDistance(
                                warehouse.getLocation(), customer.getLocation());

                // 4. Determine transport mode based on distance
                TransportMode transportMode = TransportMode.fromDistance(distanceKm);

                // 5. Default weight of 1 kg when product is not specified
                double chargeableWeightKg = 1.0;

                // 6. Calculate base shipping charge
                double baseCharge = distanceKm * chargeableWeightKg * transportMode.getRatePerKmPerKg();

                // 7. Apply delivery speed strategy
                ShippingCostStrategy strategy = strategyFactory.getStrategy(deliverySpeed);
                double totalCharge = strategy.calculate(baseCharge, chargeableWeightKg);
                double roundedCharge = Math.round(totalCharge * 100.0) / 100.0;

                log.info("Shipping: {}km, mode={}, charge=Rs{}", distanceKm, transportMode, roundedCharge);

                return ShippingChargeResponse.builder()
                                .shippingCharge(roundedCharge)
                                .distanceKm(distanceKm)
                                .transportMode(transportMode.name())
                                .deliverySpeed(deliverySpeed.getValue())
                                .warehouseId(warehouseId)
                                .customerId(customerId)
                                .build();
        }

        /**
         * Full end-to-end calculation: find nearest warehouse → compute shipping
         * charge.
         * Uses actual product weight for accurate costing.
         */
        @Override
        @Cacheable(value = "shippingCalculate", key = "'' + #request.sellerId + '-' + #request.customerId + '-' + #request.productId "
                        + "+ '-' + #request.deliverySpeed.name()")
        public ShippingCalculateResponse calculateShipping(ShippingCalculateRequest request) {

                log.debug("Full shipping calc: seller={}, product={}, customer={}, speed={}",
                                request.getSellerId(), request.getProductId(),
                                request.getCustomerId(), request.getDeliverySpeed());

                // 1. Validate customer
                Customer customer = customerRepository.findById(request.getCustomerId())
                                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));

                if (!customer.isActive()) {
                        throw new InvalidParameterException(
                                        "Customer " + request.getCustomerId() + " is not active");
                }

                // 2. Load product for its weight
                Product product = productRepository.findById(request.getProductId())
                                .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));

                // 3. Find nearest warehouse
                NearestWarehouseResponse nearestWarehouse = warehouseService.getNearestWarehouse(request.getSellerId(),
                                request.getProductId());

                // 4. Load the warehouse entity to get its location
                Warehouse warehouse = warehouseRepository.findById(nearestWarehouse.getWarehouseId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Warehouse", nearestWarehouse.getWarehouseId()));

                // 5. Calculate distance from warehouse to customer
                double distanceKm = distanceCalculatorService.calculateDistance(
                                warehouse.getLocation(), customer.getLocation());

                // 6. Determine transport mode
                TransportMode transportMode = TransportMode.fromDistance(distanceKm);

                // 7. Use chargeable weight (max of actual vs volumetric)
                double chargeableWeightKg = product.getChargeableWeightKg();

                // 8. Base charge = distance × weight × rate
                double baseCharge = distanceKm * chargeableWeightKg * transportMode.getRatePerKmPerKg();

                // 9. Apply delivery speed strategy
                ShippingCostStrategy strategy = strategyFactory.getStrategy(request.getDeliverySpeed());
                double totalCharge = strategy.calculate(baseCharge, chargeableWeightKg);
                double roundedCharge = Math.round(totalCharge * 100.0) / 100.0;

                log.info("Full shipping calc complete: seller={}, customer={}, charge=Rs{}",
                                request.getSellerId(), request.getCustomerId(), roundedCharge);

                return ShippingCalculateResponse.builder()
                                .shippingCharge(roundedCharge)
                                .nearestWarehouse(nearestWarehouse)
                                .distanceKm(distanceKm)
                                .transportMode(transportMode.name())
                                .deliverySpeed(request.getDeliverySpeed().getValue())
                                .chargeableWeightKg(chargeableWeightKg)
                                .productName(product.getProductName())
                                .build();
        }
}