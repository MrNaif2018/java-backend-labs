package com.mrnaif.javalab.service;

import java.util.Map;
import java.util.Optional;

import com.mrnaif.javalab.model.Product;
import com.mrnaif.javalab.payload.PageResponse;

public interface ProductService {

    public Product createProduct(Product product);

    public PageResponse<Product> getAllProducts(Integer page, Integer size);

    public Optional<Product> getProductById(Long id);

    public Product updateProduct(Long id, Product product);

    public Product partialUpdateProduct(Long id, Map<String, Object> updates);

    public void deleteProduct(Long id);

}