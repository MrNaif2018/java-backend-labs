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

  private List<T> content;
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;
  private boolean last;

  public List<T> getContent() {
    return content == null ? null : new ArrayList<>(this.content);
  }

  public void setContent(List<T> content) {
    if (content == null) {
      this.content = null;
    } else {
      this.content = content;
    }
  }
}
