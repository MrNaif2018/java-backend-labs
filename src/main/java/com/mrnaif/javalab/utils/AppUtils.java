package com.mrnaif.javalab.utils;

import com.mrnaif.javalab.exception.InvalidRequestException;

public class AppUtils {

  private AppUtils() {}

  public static final void validatePagination(Integer page) {

    if (page < 1) {
      throw new InvalidRequestException("Page number cannot be less than one.");
    }
  }
}
