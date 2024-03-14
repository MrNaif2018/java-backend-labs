package com.mrnaif.javalab.service;

import java.util.Map;
import java.util.Optional;

import com.mrnaif.javalab.payload.PageResponse;
import com.mrnaif.javalab.payload.product.CreateProduct;
import com.mrnaif.javalab.payload.product.DisplayProduct;

public interface ProductService {

    public DisplayProduct createProduct(CreateProduct product);

    public PageResponse<DisplayProduct> getAllProducts(Integer page, Integer size);

    public Optional<DisplayProduct> getProductById(Long id);

    public DisplayProduct updateProduct(Long id, CreateProduct product);

    public DisplayProduct partialUpdateProduct(Long id, Map<String, Object> updates);

    public void deleteProduct(Long id);

}