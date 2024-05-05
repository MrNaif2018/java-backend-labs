package com.mrnaif.javalab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CrudStats {
  private long users;
  private long stores;
  private long products;
}
