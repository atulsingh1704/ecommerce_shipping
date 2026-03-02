package com.jumbotail.ecommerce_shipping.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Represents a product listed by a seller on the marketplace.
 * Attributes like weight and dimensions directly affect shipping costs.
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Human-readable product name */
    @Column(nullable = false)
    @NotBlank(message = "Product name is required")
    private String productName;

    /** SKU code for inventory management */
    @Column(unique = true)
    private String sku;

    /** Short description of the product */
    private String description;

    /** Category (e.g., FMCG, Staples, Beverages) */
    private String category;

    /** Brand name */
    private String brand;

    /** Selling price in INR */
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be > 0")
    @Column(nullable = false)
    private double sellingPrice;

    /** MRP (maximum retail price) */
    private double mrp;

    /**
     * Weight in kilograms — critical for shipping cost calculation.
     * E.g., 0.5 for Maggie 500g, 10.0 for Rice 10Kg
     */
    @DecimalMin(value = "0.0", inclusive = false, message = "Weight must be > 0")
    @Column(nullable = false)
    private double weightKg;

    /** Length in centimeters */
    @DecimalMin("0.0")
    private double lengthCm;

    /** Width in centimeters */
    @DecimalMin("0.0")
    private double widthCm;

    /** Height in centimeters */
    @DecimalMin("0.0")
    private double heightCm;

    /**
     * Volumetric weight (in kg) based on standard formula: L×W×H / 5000
     * Used when it exceeds actual weight (courier industry standard).
     */
    public double getVolumetricWeightKg() {
        return (lengthCm * widthCm * heightCm) / 5000.0;
    }

    /**
     * Returns the chargeable weight: max of actual and volumetric.
     */
    public double getChargeableWeightKg() {
        return Math.max(weightKg, getVolumetricWeightKg());
    }

    /** The seller who owns this product listing */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @NotNull(message = "Seller is required")
    private Seller seller;

    /** Unit of measure (e.g., kg, g, litre, piece) */
    private String unitOfMeasure;

    /** Minimum order quantity */
    @Builder.Default
    private int minimumOrderQuantity = 1;

    /** Whether this product is currently listed/active */
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    /** Stock keeping units available */
    @Builder.Default
    private int stockQuantity = 0;
}