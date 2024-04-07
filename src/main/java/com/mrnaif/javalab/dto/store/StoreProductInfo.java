package com.mrnaif.javalab.dto.store;

import java.time.Instant;
import lombok.Data;

@Data
public class StoreProductInfo {
  private Long id;

  private String name;

  private Double price;

  private Long quantity;

  private String description;

  private Instant created;
}
