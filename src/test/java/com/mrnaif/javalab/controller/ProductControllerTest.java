package com.mrnaif.javalab.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.mrnaif.javalab.dto.BatchDeleteRequest;
import com.mrnaif.javalab.dto.PageResponse;
import com.mrnaif.javalab.dto.product.CreateProduct;
import com.mrnaif.javalab.dto.product.DisplayProduct;
import com.mrnaif.javalab.model.Product;
import com.mrnaif.javalab.model.User;
import com.mrnaif.javalab.service.ProductService;
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
class ProductControllerTest {
  @Mock private ProductService productService;

  @Spy private ModelMapper modelMapper = new ModelMapper();

  @InjectMocks private ProductController productController;

  private User user;
  private Product product;
  private CreateProduct createProduct;
  private DisplayProduct displayProduct;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    user =
        new User(
            1L, "test@test.com", "password", new ArrayList<>(), new ArrayList<>(), Instant.now());
    product =
        new Product(
            1L, "Test Product", 10.0, 10L, "Test Product", user, new HashSet<>(), Instant.now());
    createProduct = modelMapper.map(product, CreateProduct.class);
    displayProduct = modelMapper.map(product, DisplayProduct.class);
  }

  @Test
  void getAllProducts() {
    List<DisplayProduct> products =
        List.of(new DisplayProduct(), new DisplayProduct(), new DisplayProduct());
    PageResponse<DisplayProduct> pageResponse =
        new PageResponse<DisplayProduct>(products, 3, 1, 10, 1, false);

    when(productService.getAllProducts(1, 10)).thenReturn(pageResponse);
    when(productService.getProductsRange(1L, 1, 10)).thenReturn(pageResponse);

    ResponseEntity<PageResponse<DisplayProduct>> response =
        productController.getAllProducts(1, 10, "");
    ResponseEntity<PageResponse<DisplayProduct>> response2 =
        productController.getAllProducts(1, 10, "1");
    assertEquals(3, response2.getBody().getResult().size());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(products, response.getBody().getResult());

    verify(productService, times(1)).getAllProducts(1, 10);
    verifyNoMoreInteractions(productService);
  }

  @Test
  void getProductById() {
    Long productId = 1L;

    when(productService.getProductById(productId)).thenReturn(displayProduct);

    ResponseEntity<DisplayProduct> response = productController.getProductById(productId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(displayProduct, response.getBody());

    verify(productService, times(1)).getProductById(productId);
    verifyNoMoreInteractions(productService);
  }

  @Test
  void createProduct() {

    when(productService.createProduct(createProduct)).thenReturn(displayProduct);

    ResponseEntity<DisplayProduct> response = productController.createProduct(createProduct);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(displayProduct, response.getBody());

    verify(productService, times(1)).createProduct(createProduct);
    verifyNoMoreInteractions(productService);
  }

  @Test
  void bulkCreateProducts() {
    List<CreateProduct> createProducts =
        List.of(new CreateProduct(), new CreateProduct(), new CreateProduct());
    List<DisplayProduct> displayProducts =
        List.of(new DisplayProduct(), new DisplayProduct(), new DisplayProduct());

    when(productService.createBulkProducts(createProducts)).thenReturn(displayProducts);

    ResponseEntity<List<DisplayProduct>> response =
        productController.createBulkProducts(createProducts);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(displayProducts, response.getBody());

    verify(productService, times(1)).createBulkProducts(createProducts);
    verifyNoMoreInteractions(productService);
  }

  @Test
  void updateProduct() {
    Long productId = 1L;

    when(productService.updateProduct(productId, createProduct)).thenReturn(displayProduct);

    ResponseEntity<DisplayProduct> response =
        productController.updateProduct(productId, createProduct);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(displayProduct, response.getBody());

    verify(productService, times(1)).updateProduct(productId, createProduct);
    verifyNoMoreInteractions(productService);
  }

  @Test
  void partialUpdateProduct() {
    Long productId = 1L;

    when(productService.partialUpdateProduct(productId, new HashMap<>()))
        .thenReturn(displayProduct);

    ResponseEntity<DisplayProduct> response =
        productController.partialUpdateProduct(productId, new HashMap<>());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(displayProduct, response.getBody());

    verify(productService, times(1)).partialUpdateProduct(productId, new HashMap<>());
    verifyNoMoreInteractions(productService);
  }

  @Test
  void deleteProductById() {
    Long productId = 1L;

    ResponseEntity<Void> response = productController.deleteProduct(productId);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    verify(productService, times(1)).deleteProduct(productId);
    verifyNoMoreInteractions(productService);
  }

  @Test
  void bulkDeleteProducts() {
    List<Long> productIds = List.of(1L, 2L, 3L);

    ResponseEntity<Void> response =
        productController.deleteProducts(new BatchDeleteRequest(productIds));

    assertEquals(HttpStatus.OK, response.getStatusCode());

    verify(productService, times(1)).deleteProducts(productIds);
    verifyNoMoreInteractions(productService);
  }
} 