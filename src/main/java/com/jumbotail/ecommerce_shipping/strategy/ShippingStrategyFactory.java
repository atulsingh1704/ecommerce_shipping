package com.jumbotail.ecommerce_shipping.strategy;



import com.jumbotail.ecommerce_shipping.enums.DeliverySpeed;
import com.jumbotail.ecommerce_shipping.exception.InvalidParameterException;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Factory for resolving the correct ShippingCostStrategy based on DeliverySpeed.
 *
 * Uses Spring's dependency injection to inject all strategies by their bean name,
 * making it trivially easy to add new delivery speeds.
 */
@Component
public class ShippingStrategyFactory {

    /**
     * Map of bean name → strategy, injected by Spring.
     * Bean names match DeliverySpeed enum values (e.g., "STANDARD", "EXPRESS").
     */
    private final Map<String, ShippingCostStrategy> strategies;

    public ShippingStrategyFactory(Map<String, ShippingCostStrategy> strategies) {
        this.strategies = strategies;
    }

    /**
     * Retrieves the shipping strategy for the given delivery speed.
     *
     * @param deliverySpeed the requested delivery speed
     * @return the matching ShippingCostStrategy
     * @throws InvalidParameterException if no strategy is found
     */
    public ShippingCostStrategy getStrategy(DeliverySpeed deliverySpeed) {
        String key = deliverySpeed.name(); // "STANDARD" or "EXPRESS"
        ShippingCostStrategy strategy = strategies.get(key);
        if (strategy == null) {
            throw new InvalidParameterException(
                    "No shipping strategy found for delivery speed: " + deliverySpeed
            );
        }
        return strategy;
    }
}