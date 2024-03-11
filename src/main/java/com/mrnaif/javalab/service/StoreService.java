package com.mrnaif.javalab.service;

import java.util.Map;
import java.util.Optional;

import com.mrnaif.javalab.model.Product;
import com.mrnaif.javalab.model.Store;
import com.mrnaif.javalab.payload.PageResponse;

public interface StoreService {

    public Store createStore(Store store);

    public PageResponse<Store> getAllStores(Integer page, Integer size);

    public Optional<Store> getStoreById(Long id);

    public Store updateStore(Long id, Store store);

    public Store partialUpdateStore(Long id, Map<String, Object> updates);

    public void deleteStore(Long id);

    public Store addProductToStore(Long storeId, Product product);

    public Store removeProductFromStore(Long storeId, Long productId);

    public PageResponse<Product> getProductsInStore(Long storeId, Integer page, Integer size);

}