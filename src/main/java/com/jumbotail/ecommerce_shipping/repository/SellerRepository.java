package com.jumbotail.ecommerce_shipping.repository;


import com.jumbotail.ecommerce_shipping.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA repository for Seller entity.
 */
@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    /** Find all active sellers */
    List<Seller> findByActiveTrue();

    /** Find seller by name */
    List<Seller> findBySellerNameContainingIgnoreCase(String name);
}
