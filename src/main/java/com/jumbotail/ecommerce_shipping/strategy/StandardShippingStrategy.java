package com.jumbotail.ecommerce_shipping.strategy;



import org.springframework.stereotype.Component;

/**
 * Standard delivery shipping cost calculation.
 *
 * Formula: Rs 10 (flat courier fee) + base shipping charge
 */
@Component("STANDARD")
public class StandardShippingStrategy implements ShippingCostStrategy {

    /** Flat courier handling fee in INR */
    private static final double STANDARD_COURIER_FEE = 10.0;

    @Override
    public double calculate(double baseShippingCharge, double weightKg) {
        return STANDARD_COURIER_FEE + baseShippingCharge;
    }
}