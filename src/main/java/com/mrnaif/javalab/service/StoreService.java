package com.mrnaif.javalab.service;

import java.util.Map;
import java.util.Optional;

import com.mrnaif.javalab.dto.PageResponse;
import com.mrnaif.javalab.dto.product.DisplayProduct;
import com.mrnaif.javalab.dto.store.CreateStore;
import com.mrnaif.javalab.dto.store.DisplayStore;

public interface StoreService {

    DisplayStore createStore(CreateStore store);

    PageResponse<DisplayStore> getAllStores(Integer page, Integer size);

    Optional<DisplayStore> getStoreById(Long id);

    DisplayStore updateStore(Long id, CreateStore store);

    DisplayStore partialUpdateStore(Long id, Map<String, Object> updates);

    void deleteStore(Long id);

    DisplayStore addProductToStore(Long storeId, Long productId);

    DisplayStore removeProductFromStore(Long storeId, Long productId);

    PageResponse<DisplayProduct> getProductsRange(Long storeId, Double minPrice, Double maxPrice, Integer page,
            Integer size);

}