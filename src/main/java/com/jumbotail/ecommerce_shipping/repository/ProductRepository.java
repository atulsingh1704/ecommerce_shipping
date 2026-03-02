package com.jumbotail.ecommerce_shipping.repository;


import com.jumbotail.ecommerce_shipping.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /** Find all products belonging to a specific seller */
    List<Product> findBySellerId(Long sellerId);

    /** Find active products by seller */
    List<Product> findBySellerIdAndActiveTrue(Long sellerId);

    /** Find product by SKU */
    Optional<Product> findBySku(String sku);

    /** Find products by category */
    List<Product> findByCategoryIgnoreCase(String category);

    /**
     * Check that a product belongs to a specific seller — used for
     * nearest warehouse lookup to ensure the seller/product combo is valid.
     */
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.id = :productId AND p.seller.id = :sellerId")
    boolean existsByIdAndSellerId(@Param("productId") Long productId,
                                  @Param("sellerId") Long sellerId);
}