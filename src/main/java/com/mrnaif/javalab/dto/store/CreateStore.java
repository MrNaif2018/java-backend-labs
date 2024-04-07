package com.mrnaif.javalab.dto.store;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateStore extends BaseStore {

  private Long userId;
}
