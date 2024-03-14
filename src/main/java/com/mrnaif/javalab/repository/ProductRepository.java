package com.mrnaif.javalab.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mrnaif.javalab.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findProductsByStoresId(Long storeId, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.stores s WHERE s.id = :store AND p.price >= :minPrice AND p.price <= :maxPrice AND p.quantity > 0")
    Page<Product> findProductsByStoresIdAndPrice(@Param("store") Long storeId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice, Pageable pageable);
}