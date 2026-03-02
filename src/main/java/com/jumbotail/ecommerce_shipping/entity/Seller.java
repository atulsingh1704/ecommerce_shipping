package com.jumbotail.ecommerce_shipping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * Represents a product seller/supplier in the B2B marketplace.
 * Sellers drop their products at the nearest warehouse.
 */
@Entity
@Table(name = "sellers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Business name of the seller */
    @Column(nullable = false)
    @NotBlank(message = "Seller name is required")
    private String sellerName;

    /** Contact person at the seller's end */
    private String contactPerson;

    /** Phone number of the seller */
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
    private String phoneNumber;

    /** Seller's business email */
    private String email;

    /** Full address of the seller's warehouse/facility */
    private String address;

    /** City where seller is located */
    private String city;

    /** State */
    private String state;

    /** PIN code */
    private String pinCode;

    /** Seller's GPS coordinates – used to find nearest marketplace warehouse */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude",  column = @Column(name = "seller_lat")),
            @AttributeOverride(name = "longitude", column = @Column(name = "seller_lng"))
    })
    private Location location;

    /** GST number */
    private String gstNumber;

    /** Is the seller currently active/verified on the platform */
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    /** Rating out of 5 based on past fulfillment */
    @Builder.Default
    private double rating = 5.0;
}
