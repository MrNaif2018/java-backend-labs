package com.mrnaif.javalab.service;

import java.util.Map;
import java.util.Optional;

import com.mrnaif.javalab.dto.PageResponse;
import com.mrnaif.javalab.dto.product.CreateProduct;
import com.mrnaif.javalab.dto.product.DisplayProduct;

public interface ProductService {

    DisplayProduct createProduct(CreateProduct product);

    PageResponse<DisplayProduct> getAllProducts(Integer page, Integer size);

    Optional<DisplayProduct> getProductById(Long id);

    DisplayProduct updateProduct(Long id, CreateProduct product);

    DisplayProduct partialUpdateProduct(Long id, Map<String, Object> updates);

    void deleteProduct(Long id);

}