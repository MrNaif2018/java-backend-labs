package com.mrnaif.javalab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mrnaif.javalab.model.QRCode;

@Repository
public interface QRCodeRepository extends JpaRepository<QRCode, Long> {

}