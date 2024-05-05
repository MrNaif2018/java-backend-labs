package com.mrnaif.javalab.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BatchDeleteRequest {
  private List<Long> ids;
}
