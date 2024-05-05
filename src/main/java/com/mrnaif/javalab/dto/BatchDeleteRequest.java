package com.mrnaif.javalab.dto;

import java.util.List;
import lombok.Data;

@Data
public class BatchDeleteRequest {
  private List<Long> ids;
}
