package com.mrnaif.javalab.utils;

import com.mrnaif.javalab.exception.InvalidRequestException;

public class AppUtils {

  private AppUtils() {}

  public static final void validatePageAndSize(Integer page, Integer size) {

    if (page < 1) {
      throw new InvalidRequestException("Page number cannot be less than one.");
    }

    if (size <= 0) {
      throw new InvalidRequestException("Size cannot be less than zero.");
    }

    if (size > AppConstant.MAX_PAGE_SIZE) {
      throw new InvalidRequestException(
          "Page size must not be greater than " + AppConstant.MAX_PAGE_SIZE);
    }
  }
}
