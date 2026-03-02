package com.jumbotail.ecommerce_shipping.strategy;



/**
 * Strategy interface for calculating shipping charges.
 * Follows the Strategy design pattern to allow easy addition
 * of new delivery speed options (e.g., SAME_DAY, SCHEDULED).
 */
public interface ShippingCostStrategy {

    /**
     * Calculates the total shipping charge.
     *
     * @param baseShippingCharge the raw distance-and-weight-based charge
     * @param weightKg           chargeable weight of the product in kg
     * @return total shipping charge in INR
     */
    double calculate(double baseShippingCharge, double weightKg);
}
