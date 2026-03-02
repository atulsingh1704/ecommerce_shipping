package com.jumbotail.ecommerce_shipping.entity;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embeddable geographic location (latitude/longitude).
 * Used by Customer, Seller, and Warehouse entities.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    /**
     * Latitude: ranges from -90 (South Pole) to +90 (North Pole).
     * India: approx 8.4 to 37.6
     */
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0",  message = "Latitude must be <= 90")
    private double latitude;

    /**
     * Longitude: ranges from -180 to +180.
     * India: approx 68.7 to 97.4
     */
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0",  message = "Longitude must be <= 180")
    private double longitude;
}