package com.jumbotail.ecommerce_shipping.repository;

import com.jumbotail.ecommerce_shipping.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for Customer entity.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /** Find customer by their unique business code (e.g., "Cust-123") */
    Optional<Customer> findByCustomerCode(String customerCode);

    /** Find by phone number for authentication/lookup */
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    /** Check if a customer code is already in use */
    boolean existsByCustomerCode(String customerCode);
}