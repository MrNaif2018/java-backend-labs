package com.mrnaif.javalab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mrnaif.javalab.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}