package com.jumbotail.ecommerce_shipping.entity;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * Represents a Kirana store customer in the B2B marketplace.
 * Customers place orders for products from sellers.
 */
@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique business identifier (e.g., Cust-123) */
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Customer code is required")
    private String customerCode;

    /** Name of the Kirana store */
    @Column(nullable = false)
    @NotBlank(message = "Store name is required")
    private String storeName;

    /** Owner's full name */
    private String ownerName;

    /** 10-digit Indian mobile number */
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
    @Column(nullable = false)
    private String phoneNumber;

    /** Email for order notifications */
    private String email;

    /** Full street address */
    private String address;

    /** City of the Kirana store */
    private String city;

    /** State (e.g., Maharashtra, Karnataka) */
    private String state;

    /** PIN code */
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid PIN code")
    private String pinCode;

    /** GPS coordinates of the store */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude",  column = @Column(name = "cust_lat")),
            @AttributeOverride(name = "longitude", column = @Column(name = "cust_lng"))
    })
    private Location location;

    /** Whether the customer account is active */
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    /** GST number for business invoicing */
    private String gstNumber;

    /** Credit limit available (in Rs) */
    @Builder.Default
    private double creditLimit = 0.0;
}
