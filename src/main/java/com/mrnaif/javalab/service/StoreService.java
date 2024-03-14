package com.mrnaif.javalab.service;

import java.util.Map;
import java.util.Optional;

import com.mrnaif.javalab.payload.PageResponse;
import com.mrnaif.javalab.payload.store.CreateStore;
import com.mrnaif.javalab.payload.store.DisplayStore;

public interface StoreService {

    public DisplayStore createStore(CreateStore store);

    public PageResponse<DisplayStore> getAllStores(Integer page, Integer size);

    public Optional<DisplayStore> getStoreById(Long id);

    public DisplayStore updateStore(Long id, CreateStore store);

    public DisplayStore partialUpdateStore(Long id, Map<String, Object> updates);

    public void deleteStore(Long id);

    public DisplayStore addProductToStore(Long storeId, Long productId);

    public DisplayStore removeProductFromStore(Long storeId, Long productId);

}