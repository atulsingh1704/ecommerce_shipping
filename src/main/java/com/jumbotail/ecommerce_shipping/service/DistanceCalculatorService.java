package com.jumbotail.ecommerce_shipping.service;


import com.jumbotail.ecommerce_shipping.entity.Location;
import com.jumbotail.ecommerce_shipping.entity.Warehouse;
import com.jumbotail.ecommerce_shipping.util.HaversineUtil;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Service for distance-related calculations.
 * Wraps HaversineUtil and provides higher-level business operations.
 */
@Service
public class DistanceCalculatorService {

    /**
     * Calculates straight-line distance between two locations.
     *
     * @param from origin
     * @param to   destination
     * @return distance in kilometres
     */
    public double calculateDistance(Location from, Location to) {
        return HaversineUtil.calculateDistanceKm(from, to);
    }

    /**
     * Finds the nearest warehouse from the given location.
     *
     * @param from              seller's or source location
     * @param operationalWarehouses list of warehouses to consider
     * @return the closest Warehouse object
     * @throws IllegalStateException if the list is empty
     */
    public Warehouse findNearestWarehouse(Location from, List<Warehouse> operationalWarehouses) {
        if (operationalWarehouses == null || operationalWarehouses.isEmpty()) {
            throw new IllegalStateException("No operational warehouses available");
        }
        return operationalWarehouses.stream()
                .min(Comparator.comparingDouble(
                        wh -> HaversineUtil.calculateDistanceKm(from, wh.getLocation())
                ))
                .orElseThrow(() -> new IllegalStateException("Could not determine nearest warehouse"));
    }
}