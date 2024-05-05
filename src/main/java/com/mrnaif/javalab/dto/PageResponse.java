package com.mrnaif.javalab.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {

  private List<T> result;
  private long count;
  private int page;
  private int size;
  private int totalPages;
  private boolean last;

  public List<T> getResult() {
    return result == null ? null : new ArrayList<>(this.result);
  }

  public void setResult(List<T> result) {
    if (result == null) {
      this.result = null;
    } else {
      this.result = result;
    }
  }
}
