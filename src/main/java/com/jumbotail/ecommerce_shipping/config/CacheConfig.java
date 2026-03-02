package com.jumbotail.ecommerce_shipping.config;


import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache configuration using Caffeine for high-performance, in-process caching.
 *
 * Cached operations:
 * - nearestWarehouse    : seller-to-warehouse proximity (10 min TTL, 1000 max entries)
 * - shippingCharge      : warehouse-to-customer charge  (10 min TTL, 2000 max entries)
 * - shippingCalculate   : full end-to-end calculation   (5  min TTL, 2000 max entries)
 *
 * Caching drastically reduces latency for repeated requests with the same parameters,
 * which is common in B2B scenarios where the same seller-customer pairs order repeatedly.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                "nearestWarehouse",
                "shippingCharge",
                "shippingCalculate"
        );
        manager.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(10, TimeUnit.MINUTES)
                        .maximumSize(2000)
                        .recordStats()   // Enable cache hit/miss metrics
        );
        return manager;
    }
}
