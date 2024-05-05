package com.mrnaif.javalab.service.impl;

import com.mrnaif.javalab.dto.CrudStats;
import com.mrnaif.javalab.repository.ProductRepository;
import com.mrnaif.javalab.repository.StoreRepository;
import com.mrnaif.javalab.repository.UserRepository;
import com.mrnaif.javalab.service.CrudStatsService;
import org.springframework.stereotype.Service;

@Service
public class CrudStatsServiceImpl implements CrudStatsService {
  private UserRepository userRepository;
  private StoreRepository storeRepository;
  private ProductRepository productRepository;

  public CrudStatsServiceImpl(
      UserRepository userRepository,
      StoreRepository storeRepository,
      ProductRepository productRepository) {
    this.userRepository = userRepository;
    this.storeRepository = storeRepository;
    this.productRepository = productRepository;
  }

  @Override
  public CrudStats getCrudStats() {
    return new CrudStats(
        userRepository.count(), storeRepository.count(), productRepository.count());
  }
}
