package com.mrnaif.javalab.repository;

import com.mrnaif.javalab.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  Page<Product> findAllByOrderByCreatedDesc(Pageable pageable);

  @Query("SELECT p FROM Product p JOIN p.stores s WHERE s.id = :store ORDER BY p.created DESC")
  Page<Product> findProductsByStoresId(@Param("store") Long storeId, Pageable pageable);
}