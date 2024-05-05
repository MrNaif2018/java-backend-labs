package com.mrnaif.javalab.dto.store;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mrnaif.javalab.model.User;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper = true)
public class DisplayStore extends CreateStore {
  private Long id;

  private List<StoreProductInfo> products;

  private Instant created;

  @JsonIgnore private User user;

  @Getter(lazy = true)
  private final Long userId = user.getId();

  @Getter(lazy = true)
  private final String userEmail = user.getEmail();
}
