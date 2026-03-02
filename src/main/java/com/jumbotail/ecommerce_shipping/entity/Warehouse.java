package com.jumbotail.ecommerce_shipping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Represents a marketplace fulfillment warehouse.
 * Sellers drop products here; orders ship from here to customers.
 */
@Entity
@Table(name = "warehouses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique warehouse code (e.g., BLR_Warehouse) */
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Warehouse code is required")
    private String warehouseCode;

    /** Friendly display name */
    @Column(nullable = false)
    @NotBlank(message = "Warehouse name is required")
    private String warehouseName;

    /** Full address */
    private String address;

    /** City */
    private String city;

    /** State */
    private String state;

    /** PIN code */
    private String pinCode;

    /** GPS coordinates — used for distance calculation */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude",  column = @Column(name = "wh_lat")),
            @AttributeOverride(name = "longitude", column = @Column(name = "wh_lng"))
    })
    private Location location;

    /** Total storage capacity in cubic metres */
    private double capacityCubicMetres;

    /** Contact phone for the warehouse */
    private String contactPhone;

    /** Whether this warehouse is currently operational */
    @Column(nullable = false)
    @Builder.Default
    private boolean operational = true;
}