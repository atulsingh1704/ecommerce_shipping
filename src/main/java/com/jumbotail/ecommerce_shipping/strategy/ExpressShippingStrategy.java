package com.jumbotail.ecommerce_shipping.strategy;

import org.springframework.stereotype.Component;

/**
 * Express delivery shipping cost calculation.
 *
 * Formula: Rs 10 (flat courier fee) + Rs 1.2 per kg (express surcharge) + base shipping charge
 */
@Component("EXPRESS")
public class ExpressShippingStrategy implements ShippingCostStrategy {

    /** Flat courier handling fee in INR */
    private static final double STANDARD_COURIER_FEE = 10.0;

    /** Express surcharge per kilogram */
    private static final double EXPRESS_RATE_PER_KG = 1.2;

    @Override
    public double calculate(double baseShippingCharge, double weightKg) {
        double expressSurcharge = EXPRESS_RATE_PER_KG * weightKg;
        return STANDARD_COURIER_FEE + expressSurcharge + baseShippingCharge;
    }
}