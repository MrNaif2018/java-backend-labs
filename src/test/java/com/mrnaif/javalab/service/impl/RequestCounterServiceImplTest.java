package com.mrnaif.javalab.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RequestCounterServiceImplTest {

  @Test
  void incrementAndGetCountTest() {
    RequestCounterServiceImpl service = new RequestCounterServiceImpl();
    service.increment();
    assertEquals(1, service.getCount());
  }
}