package com.mrnaif.javalab.dto.product;

import lombok.Data;

@Data
public class BaseProduct {

  private String name;

  private Double price;

  private Long quantity;

  private String description;
}
