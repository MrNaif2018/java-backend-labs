package com.mrnaif.javalab.service;

import com.mrnaif.javalab.dto.PageResponse;
import com.mrnaif.javalab.dto.product.CreateProduct;
import com.mrnaif.javalab.dto.product.DisplayProduct;
import java.util.List;
import java.util.Map;

public interface ProductService {

  DisplayProduct createProduct(CreateProduct product);

  List<DisplayProduct> createBulkProducts(List<CreateProduct> products);

  PageResponse<DisplayProduct> getAllProducts(Integer page, Integer size);

  DisplayProduct getProductById(Long id);

  DisplayProduct updateProduct(Long id, CreateProduct product);

  DisplayProduct partialUpdateProduct(Long id, Map<String, Object> updates);

  void deleteProduct(Long id);
}