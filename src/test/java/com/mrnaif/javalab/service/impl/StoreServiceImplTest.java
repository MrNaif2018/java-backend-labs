package com.mrnaif.javalab.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.mrnaif.javalab.dto.PageResponse;
import com.mrnaif.javalab.dto.store.CreateStore;
import com.mrnaif.javalab.dto.store.DisplayStore;
import com.mrnaif.javalab.dto.store.StoreProductInfo;
import com.mrnaif.javalab.exception.InvalidRequestException;
import com.mrnaif.javalab.exception.ResourceNotFoundException;
import com.mrnaif.javalab.model.Product;
import com.mrnaif.javalab.model.Store;
import com.mrnaif.javalab.model.User;
import com.mrnaif.javalab.repository.ProductRepository;
import com.mrnaif.javalab.repository.StoreRepository;
import com.mrnaif.javalab.utils.cache.CacheFactory;
import com.mrnaif.javalab.utils.cache.GenericCache;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

  @Mock private StoreRepository storeRepository;

  @Mock private ProductRepository productRepository;

  @Spy private ModelMapper modelMapper = new ModelMapper();

  @Mock private CacheFactory cacheFactory;

  @Mock private GenericCache<Long, Store> cache;
  @Mock private GenericCache<Long, Product> productCache;

  @Mock private EntityManager entityManager;

  private PasswordEncoder passwordEncoder;

  @InjectMocks private StoreServiceImpl storeService;

  private User user;
  private Store store;
  private Product product;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    passwordEncoder = new BCryptPasswordEncoder();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    user =
        new User(
            1L,
            "test@test.com",
            passwordEncoder.encode("password"),
            new ArrayList<>(),
            new ArrayList<>(),
            Instant.now());
    store = new Store(1L, "Test Store", "store@store.com", user, new HashSet<>(), Instant.now());
    product =
        new Product(
            1L, "Test Product", 10.0, 10L, "Test Product", user, new HashSet<>(), Instant.now());
  }

  @Test
  void testCreateStore() {
    assertThrows(InvalidRequestException.class, () -> storeService.createStore(null));
    when(cache.get(1L)).thenReturn(Optional.empty());
    when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

    DisplayStore gotStore = storeService.getStoreById(1L);

    assertEquals(modelMapper.map(store, DisplayStore.class), gotStore);
    verify(cache, times(1)).put(1L, store);
  }

  @Test
  void testGetStoreByIdCacheHit() {
    when(cache.get(1L)).thenReturn(Optional.of(store));

    DisplayStore gotStore = storeService.getStoreById(1L);

    assertEquals(modelMapper.map(store, DisplayStore.class), gotStore);
    verify(storeRepository, never()).findById(anyLong());
    verify(cache, times(1)).put(1L, store);
  }

  @Test
  void testGetStoreByIdCacheMiss() {
    when(cache.get(1L)).thenReturn(Optional.empty());
    when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

    DisplayStore gotStore = storeService.getStoreById(1L);

    assertEquals(modelMapper.map(store, DisplayStore.class), gotStore);
    verify(cache).put(1L, store);
  }

  @Test
  void testGetStoreByIdNotFound() {
    when(cache.get(1L)).thenReturn(Optional.empty());
    when(storeRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> storeService.getStoreById(1L));
    verify(cache, never()).put(anyLong(), any(Store.class));
  }

  @Test
  void testGetAllStores() {
    List<Store> stores = List.of(store, store, store);
    Page<Store> pagedStores = new PageImpl<Store>(stores);
    when(storeRepository.findAllByOrderByCreatedDesc(any(Pageable.class))).thenReturn(pagedStores);
    PageResponse<DisplayStore> response = storeService.getAllStores(1, 10);
    PageResponse<DisplayStore> response2 = storeService.getAllStores(1, -1);
    response2.setSize(10);
    assertEquals(response, response2);
    List<DisplayStore> result = response.getResult();

    assertEquals(1, response.getPage());
    assertEquals(10, response.getSize());
    assertEquals(3, response.getCount());
    assertEquals(3, result.size());
    assertEquals(modelMapper.map(store, DisplayStore.class), result.get(1));
  }

  @Test
  void testSaveStore() {
    when(storeRepository.save(any(Store.class))).thenReturn(store);

    DisplayStore saved =
        modelMapper.map(
            storeService.createStore(modelMapper.map(user, CreateStore.class)), DisplayStore.class);

    assertNotNull(saved);
    assertEquals(modelMapper.map(store, DisplayStore.class), saved);
  }

  @Test
  void testUnexpectedUpdateStore() {
    CreateStore unexpected = new CreateStore();
    unexpected.setUserId(0L);
    when(storeRepository.saveAndFlush(any(Store.class))).thenThrow(new RuntimeException());
    when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
    assertThrows(InvalidRequestException.class, () -> storeService.partialUpdateStore(1L, null));
    assertThrows(InvalidRequestException.class, () -> storeService.updateStore(1L, unexpected));
  }

  @Test
  void testUpdateStore() {
    when(entityManager.find(Store.class, 1L)).thenReturn(store);
    DisplayStore updated =
        modelMapper.map(
            storeService.updateStore(store.getId(), modelMapper.map(store, CreateStore.class)),
            DisplayStore.class);

    assertNotNull(updated);
    assertEquals(modelMapper.map(store, DisplayStore.class), updated);
    verify(cache, times(1)).invalidate(1L);
  }

  @Test
  void testPartialUpdateStore() {
    assertNull(storeService.partialUpdateStore(1L, new HashMap<>()));
    when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
    Instant now = Instant.now();
    DisplayStore updated =
        modelMapper.map(
            storeService.partialUpdateStore(
                store.getId(),
                new HashMap<>() {
                  {
                    put("name", "New Store");
                    put("created", now);
                  }
                }),
            DisplayStore.class);

    assertNotNull(updated);
    assertEquals("New Store", updated.getName());
    assertEquals(now, updated.getCreated());
    verify(cache, times(1)).invalidate(1L);
  }

  @Test
  void testPartialUpdateStoreWithProducts() {
    store.addProduct(product);
    when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
    DisplayStore updated =
        modelMapper.map(
            storeService.partialUpdateStore(
                store.getId(),
                new HashMap<>() {
                  {
                    put("products", List.of(product.getId().intValue()));
                    put("userEmail", "");
                  }
                }),
            DisplayStore.class);

    assertNotNull(updated);
    assertEquals(modelMapper.map(product, StoreProductInfo.class), updated.getProducts().get(0));
    verify(cache, times(1)).invalidate(1L);
    verify(productCache, times(2)).invalidate(1L);
  }

  @Test
  void testBulkAddStores() {
    assertThrows(InvalidRequestException.class, () -> storeService.createBulkStores(null));

    List<CreateStore> stores = List.of(modelMapper.map(store, CreateStore.class));
    when(storeRepository.save(any(Store.class))).thenReturn(store);

    List<DisplayStore> savedStores = storeService.createBulkStores(stores);

    assertEquals(1, savedStores.size());
    assertEquals(modelMapper.map(store, DisplayStore.class), savedStores.get(0));
  }

  @Test
  void testDeleteStore() {
    assertThrows(ResourceNotFoundException.class, () -> storeService.deleteStore(1L));
    doNothing().when(storeRepository).deleteById(1L);
    when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
    storeService.deleteStore(1L);
    verify(storeRepository, times(1)).deleteById(1L);
    verify(cache, times(1)).invalidate(1L);
  }

  @Test
  void testDeleteStoreWithProduct() {
    when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
    store.addProduct(product);
    storeService.deleteStore(1L);
    verify(storeRepository, times(1)).deleteById(1L);
    verify(productCache, times(1)).invalidate(1L);
    verify(cache, times(1)).invalidate(1L);
  }

  @Test
  void testAddProductToStore() {
    assertThrows(ResourceNotFoundException.class, () -> storeService.addProductToStore(1L, 1L));
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
    assertThrows(InvalidRequestException.class, () -> storeService.addProductToStore(1L, 999L));
    store.addProduct(product);
    when(storeRepository.save(any(Store.class))).thenReturn(store);

    DisplayStore result = storeService.addProductToStore(store.getId(), product.getId());

    assertEquals(modelMapper.map(store, DisplayStore.class), result);
    verify(cache, times(1)).invalidate(1L);

    store.removeProduct(product.getId());
  }

  @Test
  void testRemoveProductFromStore() {
    assertThrows(
        ResourceNotFoundException.class, () -> storeService.removeProductFromStore(1L, 1L));
    when(storeRepository.save(any(Store.class))).thenReturn(store);
    store.addProduct(product);
    when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

    DisplayStore result = storeService.removeProductFromStore(store.getId(), product.getId());
    store.removeProduct(product.getId());

    assertEquals(modelMapper.map(store, DisplayStore.class), result);
    verify(cache, times(1)).invalidate(1L);
  }

  @Test
  void testGetStoresRange() {
    List<Store> stores = List.of(store, store, store);
    Page<Store> pagedStores = new PageImpl<Store>(stores);
    when(storeRepository.findStoresByProductId(1L, PageRequest.of(0, 10))).thenReturn(pagedStores);
    when(storeRepository.findStoresByProductId(1L, Pageable.unpaged())).thenReturn(pagedStores);

    PageResponse<DisplayStore> response = storeService.getStoresRange(1L, 1, 10);
    PageResponse<DisplayStore> response2 = storeService.getStoresRange(1L, 1, -1);
    response2.setSize(10);
    assertEquals(response, response2);
    List<DisplayStore> result = response.getResult();

    assertEquals(1, response.getPage());
    assertEquals(10, response.getSize());
    assertEquals(3, response.getCount());
    assertEquals(3, result.size());
    assertEquals(modelMapper.map(store, DisplayStore.class), result.get(1));
  }

  @Test
  void testBulkDeleteStores() {
    List<Long> ids = List.of(1L, 2L, 3L);
    doNothing().when(storeRepository).deleteById(anyLong());
    when(storeRepository.findById(anyLong())).thenReturn(Optional.of(store));
    storeService.deleteStores(ids);
    verify(storeRepository, times(3)).deleteById(anyLong());
    verify(cache, times(3)).invalidate(anyLong());
  }
}
