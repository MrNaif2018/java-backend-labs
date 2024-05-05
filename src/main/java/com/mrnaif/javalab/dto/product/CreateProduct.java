package com.mrnaif.javalab.dto.product;

import lombok.Data;

@Data
public class CreateProduct {
  private String name;

  private Double price;

  private Long quantity;

  private String description;

  private Long userId;
}
