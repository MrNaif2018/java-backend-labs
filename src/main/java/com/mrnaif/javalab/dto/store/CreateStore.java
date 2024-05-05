package com.mrnaif.javalab.dto.store;

import lombok.Data;

@Data
public class CreateStore {
  private String name;
  private String email;

  private Long userId;
}
