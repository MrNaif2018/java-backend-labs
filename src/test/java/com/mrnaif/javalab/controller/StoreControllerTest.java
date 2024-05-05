package com.mrnaif.javalab.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.mrnaif.javalab.dto.BatchDeleteRequest;
import com.mrnaif.javalab.dto.PageResponse;
import com.mrnaif.javalab.dto.store.CreateStore;
import com.mrnaif.javalab.dto.store.DisplayStore;
import com.mrnaif.javalab.model.Store;
import com.mrnaif.javalab.model.User;
import com.mrnaif.javalab.service.StoreService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class StoreControllerTest {
  @Mock private StoreService storeService;

  @Spy private ModelMapper modelMapper = new ModelMapper();

  @InjectMocks private StoreController storeController;

  private User user;
  private Store store;
  private CreateStore createStore;
  private DisplayStore displayStore;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    user =
        new User(
            1L, "test@test.com", "password", new ArrayList<>(), new ArrayList<>(), Instant.now());
    store = new Store(1L, "Test Store", "store@store.com", user, new HashSet<>(), Instant.now());
    createStore = modelMapper.map(store, CreateStore.class);
    displayStore = modelMapper.map(store, DisplayStore.class);
  }

  @Test
  void getAllStores() {
    List<DisplayStore> stores = List.of(new DisplayStore(), new DisplayStore(), new DisplayStore());
    PageResponse<DisplayStore> pageResponse =
        new PageResponse<DisplayStore>(stores, 3, 1, 10, 1, false);

    when(storeService.getAllStores(1, 10)).thenReturn(pageResponse);
    when(storeService.getStoresRange(1L, 1, 10)).thenReturn(pageResponse);

    ResponseEntity<PageResponse<DisplayStore>> response = storeController.getAllStores(1, 10, "");
    ResponseEntity<PageResponse<DisplayStore>> response2 = storeController.getAllStores(1, 10, "1");
    assertEquals(3, response2.getBody().getResult().size());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(stores, response.getBody().getResult());

    verify(storeService, times(1)).getAllStores(1, 10);
    verifyNoMoreInteractions(storeService);
  }

  @Test
  void getStoreById() {
    Long storeId = 1L;

    when(storeService.getStoreById(storeId)).thenReturn(displayStore);

    ResponseEntity<DisplayStore> response = storeController.getStoreById(storeId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(displayStore, response.getBody());

    verify(storeService, times(1)).getStoreById(storeId);
    verifyNoMoreInteractions(storeService);
  }

  @Test
  void createStore() {

    when(storeService.createStore(createStore)).thenReturn(displayStore);

    ResponseEntity<DisplayStore> response = storeController.createStore(createStore);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(displayStore, response.getBody());

    verify(storeService, times(1)).createStore(createStore);
    verifyNoMoreInteractions(storeService);
  }

  @Test
  void bulkCreateStores() {
    List<CreateStore> createStores =
        List.of(new CreateStore(), new CreateStore(), new CreateStore());
    List<DisplayStore> displayStores =
        List.of(new DisplayStore(), new DisplayStore(), new DisplayStore());

    when(storeService.createBulkStores(createStores)).thenReturn(displayStores);

    ResponseEntity<List<DisplayStore>> response = storeController.createBulkStores(createStores);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(displayStores, response.getBody());

    verify(storeService, times(1)).createBulkStores(createStores);
    verifyNoMoreInteractions(storeService);
  }

  @Test
  void bulkDeleteStores() {
    List<Long> storeIds = List.of(1L, 2L, 3L);

    ResponseEntity<Void> response = storeController.deleteStores(new BatchDeleteRequest(storeIds));

    assertEquals(HttpStatus.OK, response.getStatusCode());

    verify(storeService, times(1)).deleteStores(storeIds);
    verifyNoMoreInteractions(storeService);
  }

  @Test
  void updateStore() {
    Long storeId = 1L;

    when(storeService.updateStore(storeId, createStore)).thenReturn(displayStore);

    ResponseEntity<DisplayStore> response = storeController.updateStore(storeId, createStore);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(displayStore, response.getBody());

    verify(storeService, times(1)).updateStore(storeId, createStore);
    verifyNoMoreInteractions(storeService);
  }

  @Test
  void partialUpdateStore() {
    Long storeId = 1L;

    when(storeService.partialUpdateStore(storeId, new HashMap<>())).thenReturn(displayStore);

    ResponseEntity<DisplayStore> response =
        storeController.partialUpdateStore(storeId, new HashMap<>());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(displayStore, response.getBody());

    verify(storeService, times(1)).partialUpdateStore(storeId, new HashMap<>());
    verifyNoMoreInteractions(storeService);
  }

  @Test
  void deleteStoreById() {
    Long storeId = 1L;

    ResponseEntity<Void> response = storeController.deleteStore(storeId);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    verify(storeService, times(1)).deleteStore(storeId);
    verifyNoMoreInteractions(storeService);
  }
} 