package com.warehouse.repository;

import com.warehouse.dto.response.ProductResponse;
import com.warehouse.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findBySku(String sku);

    Page<ProductEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
