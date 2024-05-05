package com.mrnaif.javalab.repository;

import com.mrnaif.javalab.model.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
  public Page<Store> findAllByOrderByCreatedDesc(Pageable pageable);

  @Query("SELECT s FROM Store s JOIN s.products p WHERE p.id = :product ORDER BY s.created DESC")
  Page<Store> findStoresByProductId(@Param("product") Long productId, Pageable pageable);
}
