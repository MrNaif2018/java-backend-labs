package com.mrnaif.javalab.dto.product;

import java.time.Instant;
import lombok.Data;

@Data
public class ProductStoreInfo {
  private Long id;

  private String name;

  private String email;

  private Instant created;
}
