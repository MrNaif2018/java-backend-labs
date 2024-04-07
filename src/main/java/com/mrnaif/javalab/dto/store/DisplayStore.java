package com.mrnaif.javalab.dto.store;

import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DisplayStore extends BaseStore {
  private Long id;

  private List<StoreProductInfo> products;

  private Instant created;
}
