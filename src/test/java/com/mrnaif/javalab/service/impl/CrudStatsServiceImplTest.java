package com.mrnaif.javalab.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.mrnaif.javalab.dto.CrudStats;
import com.mrnaif.javalab.repository.ProductRepository;
import com.mrnaif.javalab.repository.StoreRepository;
import com.mrnaif.javalab.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CrudStatsServiceImplTest {

  private UserRepository userRepository;
  private StoreRepository storeRepository;
  private ProductRepository productRepository;
  private CrudStatsServiceImpl crudStatsService;

  @BeforeEach
  public void setup() {
    userRepository = Mockito.mock(UserRepository.class);
    storeRepository = Mockito.mock(StoreRepository.class);
    productRepository = Mockito.mock(ProductRepository.class);
    crudStatsService = new CrudStatsServiceImpl(userRepository, storeRepository, productRepository);
  }

  @Test
  void testGetCrudStats() {
    when(userRepository.count()).thenReturn(10L);
    when(storeRepository.count()).thenReturn(20L);
    when(productRepository.count()).thenReturn(30L);

    CrudStats stats = crudStatsService.getCrudStats();

    assertEquals(10L, stats.getUsers());
    assertEquals(20L, stats.getStores());
    assertEquals(30L, stats.getProducts());
  }
}