package com.jumbotail.ecommerce_shipping.enums;

/**
 * Transport modes and their associated per-km-per-kg rates.
 *
 * | Mode       | Distance Range | Rate (Rs/km/kg) |
 * |------------|---------------|-----------------|
 * | MINI_VAN   | 0 – 100 km    | 3               |
 * | TRUCK      | 100 – 500 km  | 2               |
 * | AEROPLANE  | 500 km+       | 1               |
 */
public enum TransportMode {

    MINI_VAN(3.0, 0, 100),
    TRUCK(2.0, 100, 500),
    AEROPLANE(1.0, 500, Integer.MAX_VALUE);

    /** Rate in Rs per km per kg */
    private final double ratePerKmPerKg;

    /** Lower bound of distance range (inclusive, in km) */
    private final double minDistanceKm;

    /** Upper bound of distance range (exclusive, in km) */
    private final double maxDistanceKm;

    TransportMode(double ratePerKmPerKg, double minDistanceKm, double maxDistanceKm) {
        this.ratePerKmPerKg = ratePerKmPerKg;
        this.minDistanceKm  = minDistanceKm;
        this.maxDistanceKm  = maxDistanceKm;
    }

    public double getRatePerKmPerKg() {
        return ratePerKmPerKg;
    }

    /**
     * Determines the appropriate transport mode based on distance.
     *
     * @param distanceKm the distance from warehouse to customer
     * @return the applicable TransportMode
     */
    public static TransportMode fromDistance(double distanceKm) {
        for (TransportMode mode : values()) {
            if (distanceKm >= mode.minDistanceKm && distanceKm < mode.maxDistanceKm) {
                return mode;
            }
        }
        // Fallback – should never happen given ranges cover 0 to ∞
        return AEROPLANE;
    }
}
