package com.mrnaif.javalab.dto.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mrnaif.javalab.model.User;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper = true)
public class DisplayProduct extends CreateProduct {

  private Long id;

  private Set<ProductStoreInfo> stores = new HashSet<>();

  @JsonIgnore private User user;

  @Getter(lazy = true)
  private final Long userId = user.getId();

  @Getter(lazy = true)
  private final String userEmail = user.getEmail();

  private Instant created;
}
