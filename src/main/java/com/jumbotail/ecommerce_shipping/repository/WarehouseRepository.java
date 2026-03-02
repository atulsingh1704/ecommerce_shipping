package com.jumbotail.ecommerce_shipping.repository;


import com.jumbotail.ecommerce_shipping.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for Warehouse entity.
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    /** Returns only warehouses that are currently operational */
    List<Warehouse> findByOperationalTrue();

    /** Find by unique code */
    Optional<Warehouse> findByWarehouseCode(String warehouseCode);
}