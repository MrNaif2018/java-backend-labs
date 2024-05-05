package com.mrnaif.javalab.service;

import com.mrnaif.javalab.dto.PageResponse;
import com.mrnaif.javalab.dto.store.CreateStore;
import com.mrnaif.javalab.dto.store.DisplayStore;
import java.util.List;
import java.util.Map;

public interface StoreService {

  DisplayStore createStore(CreateStore store);

  List<DisplayStore> createBulkStores(List<CreateStore> stores);

  PageResponse<DisplayStore> getAllStores(Integer page, Integer size);

  DisplayStore getStoreById(Long id);

  DisplayStore updateStore(Long id, CreateStore store);

  DisplayStore partialUpdateStore(Long id, Map<String, Object> updates);

  void deleteStore(Long id);

  DisplayStore addProductToStore(Long storeId, Long productId);

  DisplayStore removeProductFromStore(Long storeId, Long productId);

  PageResponse<DisplayStore> getStoresRange(Long productId, Integer page, Integer size);

  void deleteStores(List<Long> ids);
}