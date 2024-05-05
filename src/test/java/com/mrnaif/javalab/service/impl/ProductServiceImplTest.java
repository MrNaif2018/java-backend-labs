package com.mrnaif.javalab.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.mrnaif.javalab.dto.PageResponse;
import com.mrnaif.javalab.dto.product.CreateProduct;
import com.mrnaif.javalab.dto.product.DisplayProduct;
import com.mrnaif.javalab.dto.product.ProductStoreInfo;
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
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
  @Mock private CacheFactory cacheFactory;

  @Mock private ProductRepository productRepository;
  @Mock private StoreRepository storeRepository;

  @Mock private EntityManager entityManager;

  @Spy private ModelMapper modelMapper = new ModelMapper();

  @Mock private GenericCache<Long, Product> cache;
  @Mock private GenericCache<Long, Store> storeCache;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private ProductServiceImpl productService;

  private User user;
  private Store store;
  private Product product;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
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
  void testCreateProduct() {
    assertThrows(InvalidRequestException.class, () -> productService.createProduct(null));
    when(cache.get(1L)).thenReturn(Optional.empty());
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    DisplayProduct gotProduct = productService.getProductById(1L);

    assertEquals(modelMapper.map(product, DisplayProduct.class), gotProduct);
    verify(cache, times(1)).put(1L, product);
  }

  @Test
  void testGetProductByIdCacheHit() {
    when(cache.get(1L)).thenReturn(Optional.of(product));

    DisplayProduct gotProduct = productService.getProductById(1L);

    assertEquals(modelMapper.map(product, DisplayProduct.class), gotProduct);
    verify(productRepository, never()).findById(anyLong());
    verify(cache, times(1)).put(1L, product);
  }

  @Test
  void testGetProductByIdCacheMiss() {
    when(cache.get(1L)).thenReturn(Optional.empty());
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    DisplayProduct gotProduct = productService.getProductById(1L);

    assertEquals(modelMapper.map(product, DisplayProduct.class), gotProduct);
    verify(cache).put(1L, product);
  }

  @Test
  void testGetProductByIdNotFound() {
    when(cache.get(1L)).thenReturn(Optional.empty());
    when(productRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
    verify(cache, never()).put(anyLong(), any(Product.class));
  }

  @Test
  void testGetAllProducts() {
    List<Product> products = List.of(product, product, product);
    Page<Product> pagedProducts = new PageImpl<Product>(products);
    when(productRepository.findAllByOrderByCreatedDesc(any(Pageable.class)))
        .thenReturn(pagedProducts);
    PageResponse<DisplayProduct> response = productService.getAllProducts(1, 10);
    List<DisplayProduct> result = response.getResult();

    assertEquals(1, response.getPage());
    assertEquals(10, response.getSize());
    assertEquals(3, response.getCount());
    assertEquals(3, result.size());
    assertEquals(modelMapper.map(product, DisplayProduct.class), result.get(1));
  }

  @Test
  void testSaveProduct() {
    when(productRepository.save(any(Product.class))).thenReturn(product);

    DisplayProduct saved =
        modelMapper.map(
            productService.createProduct(modelMapper.map(product, CreateProduct.class)),
            DisplayProduct.class);

    assertNotNull(saved);
    assertEquals(modelMapper.map(product, DisplayProduct.class), saved);
  }

  @Test
  void testUnexpectedUpdateProduct() {
    CreateProduct unexpected = new CreateProduct();
    unexpected.setUserId(0L);
    when(productRepository.saveAndFlush(any(Product.class))).thenThrow(new RuntimeException());
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    assertThrows(
        InvalidRequestException.class, () -> productService.partialUpdateProduct(1L, null));
    assertThrows(InvalidRequestException.class, () -> productService.updateProduct(1L, unexpected));
  }

  @Test
  void testUpdateProduct() {
    when(entityManager.find(Product.class, 1L)).thenReturn(product);
    DisplayProduct updated =
        modelMapper.map(
            productService.updateProduct(
                product.getId(), modelMapper.map(product, CreateProduct.class)),
            DisplayProduct.class);

    assertNotNull(updated);
    assertEquals(modelMapper.map(product, DisplayProduct.class), updated);
    verify(cache, times(1)).invalidate(1L);
  }

  @Test
  void testPartialUpdateProduct() {
    assertNull(productService.partialUpdateProduct(1L, new HashMap<>()));
    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
    Instant now = Instant.now();
    DisplayProduct updated =
        modelMapper.map(
            productService.partialUpdateProduct(
                product.getId(),
                new HashMap<>() {
                  {
                    put("description", "Test");
                    put("created", now);
                  }
                }),
            DisplayProduct.class);

    assertNotNull(updated);
    assertEquals("Test", updated.getDescription());
    assertEquals(now, updated.getCreated());
    verify(cache, times(1)).invalidate(1L);
  }

  @Test
  void testPartialUpdateProductWithStores() {
    product.addStore(store);
    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
    when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
    DisplayProduct updated =
        modelMapper.map(
            productService.partialUpdateProduct(
                product.getId(),
                new HashMap<>() {
                  {
                    put("stores", List.of(store.getId().intValue()));
                    put("userEmail", "");
                  }
                }),
            DisplayProduct.class);

    assertNotNull(updated);
    assertEquals(
        modelMapper.map(store, ProductStoreInfo.class), updated.getStores().iterator().next());
    verify(cache, times(1)).invalidate(1L);
    verify(storeCache, times(2)).invalidate(1L);
  }

  @Test
  void testBulkAddProducts() {
    assertThrows(InvalidRequestException.class, () -> productService.createBulkProducts(null));

    List<CreateProduct> products = List.of(modelMapper.map(product, CreateProduct.class));
    when(productRepository.save(any(Product.class))).thenReturn(product);

    List<DisplayProduct> savedProducts = productService.createBulkProducts(products);

    assertEquals(1, savedProducts.size());
    assertEquals(modelMapper.map(product, DisplayProduct.class), savedProducts.get(0));
  }

  @Test
  void testDeleteProduct() {
    assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
    doNothing().when(productRepository).deleteById(1L);
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    productService.deleteProduct(1L);
    verify(productRepository, times(1)).deleteById(1L);
    verify(cache, times(1)).invalidate(1L);
  }

  @Test
  void testDeleteProductWithStore() {
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    Store store =
        new Store(1L, "Test Store", "store@store.com", user, new HashSet<>(), Instant.now());
    store.addProduct(product);
    productService.deleteProduct(1L);
    verify(productRepository, times(1)).deleteById(1L);
    verify(storeCache, times(1)).invalidate(1L);
    verify(cache, times(1)).invalidate(1L);
  }

  @Test
  void testGetProductsRange() {
    List<Product> products = List.of(product, product, product);
    Page<Product> pagedProducts = new PageImpl<Product>(products);
    when(productRepository.findProductsByStoresId(1L, PageRequest.of(0, 10)))
        .thenReturn(pagedProducts);
    when(productRepository.findProductsByStoresId(1L, Pageable.unpaged()))
        .thenReturn(pagedProducts);

    PageResponse<DisplayProduct> response = productService.getProductsRange(1L, 1, 10);
    PageResponse<DisplayProduct> response2 = productService.getProductsRange(1L, 1, -1);
    response2.setSize(10);
    assertEquals(response, response2);
    List<DisplayProduct> result = response.getResult();

    assertEquals(1, response.getPage());
    assertEquals(10, response.getSize());
    assertEquals(3, response.getCount());
    assertEquals(3, result.size());
    assertEquals(modelMapper.map(product, DisplayProduct.class), result.get(1));
  }

  @Test
  void testBulkDeleteProducts() {
    List<Long> ids = List.of(1L, 2L, 3L);
    doNothing().when(productRepository).deleteById(anyLong());
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
    productService.deleteProducts(ids);
    verify(productRepository, times(3)).deleteById(anyLong());
    verify(cache, times(3)).invalidate(anyLong());
  }
}
