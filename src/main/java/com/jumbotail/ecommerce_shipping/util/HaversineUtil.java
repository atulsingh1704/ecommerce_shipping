package com.jumbotail.ecommerce_shipping.util;


import com.jumbotail.ecommerce_shipping.entity.Location;

/**
 * Utility class for calculating great-circle distance between two GPS coordinates
 * using the Haversine formula.
 *
 * The Haversine formula accounts for the curvature of the Earth and is widely
 * used in logistics applications where accuracy matters over short/medium distances.
 */
public final class HaversineUtil {

    /** Mean radius of the Earth in kilometres */
    private static final double EARTH_RADIUS_KM = 6371.0;

    // Private constructor — utility class should not be instantiated
    private HaversineUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Calculates the straight-line (great-circle) distance between two locations.
     *
     * @param from origin location
     * @param to   destination location
     * @return distance in kilometres, rounded to 2 decimal places
     */
    public static double calculateDistanceKm(Location from, Location to) {
        double lat1 = Math.toRadians(from.getLatitude());
        double lat2 = Math.toRadians(to.getLatitude());
        double deltaLat = Math.toRadians(to.getLatitude() - from.getLatitude());
        double deltaLng = Math.toRadians(to.getLongitude() - from.getLongitude());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanceKm = EARTH_RADIUS_KM * c;

        // Round to 2 decimal places
        return Math.round(distanceKm * 100.0) / 100.0;
    }
}