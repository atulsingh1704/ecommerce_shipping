package com.jumbotail.ecommerce_shipping.service.impl;


import com.jumbotail.ecommerce_shipping.dto.response.LocationDto;
import com.jumbotail.ecommerce_shipping.dto.response.NearestWarehouseResponse;
import com.jumbotail.ecommerce_shipping.entity.Product;
import com.jumbotail.ecommerce_shipping.entity.Seller;
import com.jumbotail.ecommerce_shipping.entity.Warehouse;
import com.jumbotail.ecommerce_shipping.exception.InvalidParameterException;
import com.jumbotail.ecommerce_shipping.exception.NoWarehouseFoundException;
import com.jumbotail.ecommerce_shipping.exception.ResourceNotFoundException;
import com.jumbotail.ecommerce_shipping.repository.ProductRepository;
import com.jumbotail.ecommerce_shipping.repository.SellerRepository;
import com.jumbotail.ecommerce_shipping.repository.WarehouseRepository;
import com.jumbotail.ecommerce_shipping.service.DistanceCalculatorService;
import com.jumbotail.ecommerce_shipping.service.WarehouseService;
import com.jumbotail.ecommerce_shipping.util.HaversineUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of WarehouseService.
 *
 * Core logic:
 * 1. Validate sellerId and productId (product must belong to seller)
 * 2. Load all operational warehouses
 * 3. Use Haversine formula to find the closest one to the seller's location
 * 4. Return warehouse details + distance
 */
@Service
public class WarehouseServiceImpl implements WarehouseService {

    private static final Logger log = LoggerFactory.getLogger(WarehouseServiceImpl.class);

    private final SellerRepository     sellerRepository;
    private final ProductRepository    productRepository;
    private final WarehouseRepository  warehouseRepository;
    private final DistanceCalculatorService distanceCalculatorService;

    public WarehouseServiceImpl(SellerRepository sellerRepository,
                                ProductRepository productRepository,
                                WarehouseRepository warehouseRepository,
                                DistanceCalculatorService distanceCalculatorService) {
        this.sellerRepository          = sellerRepository;
        this.productRepository         = productRepository;
        this.warehouseRepository       = warehouseRepository;
        this.distanceCalculatorService = distanceCalculatorService;
    }

    /**
     * Cached by sellerId + productId combination.
     * Cache is invalidated after 10 minutes (configured in CacheConfig).
     */
    @Override
    @Cacheable(value = "nearestWarehouse", key = "#sellerId + '-' + #productId")
    public NearestWarehouseResponse getNearestWarehouse(Long sellerId, Long productId) {
        log.debug("Finding nearest warehouse for sellerId={}, productId={}", sellerId, productId);

        // 1. Validate input IDs
        if (sellerId == null || sellerId <= 0) {
            throw new InvalidParameterException("sellerId must be a positive number");
        }
        if (productId == null || productId <= 0) {
            throw new InvalidParameterException("productId must be a positive number");
        }

        // 2. Load seller
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", sellerId));

        if (!seller.isActive()) {
            throw new InvalidParameterException("Seller with ID " + sellerId + " is not active");
        }

        // 3. Load product and verify it belongs to this seller
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new InvalidParameterException(
                    "Product " + productId + " does not belong to Seller " + sellerId
            );
        }

        if (!product.isActive()) {
            throw new InvalidParameterException("Product with ID " + productId + " is not active");
        }

        // 4. Load all operational warehouses
        List<Warehouse> warehouses = warehouseRepository.findByOperationalTrue();
        if (warehouses.isEmpty()) {
            throw new NoWarehouseFoundException(
                    "No operational warehouses available in the system"
            );
        }

        // 5. Find nearest warehouse using Haversine
        Warehouse nearest = distanceCalculatorService.findNearestWarehouse(
                seller.getLocation(), warehouses
        );

        double distanceKm = HaversineUtil.calculateDistanceKm(
                seller.getLocation(), nearest.getLocation()
        );

        log.info("Nearest warehouse for seller {} is {} ({} km away)",
                sellerId, nearest.getWarehouseCode(), distanceKm);

        // 6. Build and return response
        return NearestWarehouseResponse.builder()
                .warehouseId(nearest.getId())
                .warehouseCode(nearest.getWarehouseCode())
                .warehouseName(nearest.getWarehouseName())
                .warehouseLocation(new LocationDto(
                        nearest.getLocation().getLatitude(),
                        nearest.getLocation().getLongitude()
                ))
                .distanceFromSellerKm(distanceKm)
                .build();
    }
}