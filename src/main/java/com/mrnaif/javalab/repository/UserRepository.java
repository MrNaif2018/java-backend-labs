package com.mrnaif.javalab.repository;

import com.mrnaif.javalab.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  public Page<User> findAllByOrderByCreatedDesc(Pageable pageable);
}
