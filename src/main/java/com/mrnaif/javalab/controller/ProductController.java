package com.mrnaif.javalab.controller;

import com.mrnaif.javalab.aop.annotation.RequestStats;
import com.mrnaif.javalab.dto.PageResponse;
import com.mrnaif.javalab.dto.product.CreateProduct;
import com.mrnaif.javalab.dto.product.DisplayProduct;
import com.mrnaif.javalab.service.ProductService;
import com.mrnaif.javalab.utils.AppConstant;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequestStats
@CrossOrigin(origins = "*")
public class ProductController {

  private ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  public ResponseEntity<DisplayProduct> createProduct(@RequestBody CreateProduct product) {
    return ResponseEntity.ok(productService.createProduct(product));
  }

  @PostMapping("/bulk")
  public ResponseEntity<List<DisplayProduct>> createBulkProducts(
      @RequestBody List<CreateProduct> products) {
    return ResponseEntity.ok(productService.createBulkProducts(products));
  }

  @GetMapping
  public ResponseEntity<PageResponse<DisplayProduct>> getAllProducts(
      @RequestParam(
              value = "page",
              required = false,
              defaultValue = AppConstant.DEFAULT_PAGE_NUMBER)
          Integer page,
      @RequestParam(value = "size", required = false, defaultValue = AppConstant.DEFAULT_PAGE_SIZE)
          Integer size) {
    return ResponseEntity.ok(productService.getAllProducts(page, size));
  }

  @GetMapping("/{id}")
  public ResponseEntity<DisplayProduct> getProductById(@PathVariable Long id) {
    // of allows to return 404 if optional is not present()
    return ResponseEntity.ok(productService.getProductById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<DisplayProduct> updateProduct(
      @PathVariable Long id, @RequestBody CreateProduct product) {
    return ResponseEntity.ok(productService.updateProduct(id, product));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<DisplayProduct> partialUpdateProduct(
      @PathVariable Long id, @RequestBody Map<String, Object> updates) {
    return ResponseEntity.ok(productService.partialUpdateProduct(id, updates));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.deleteProduct(id);
    return ResponseEntity.ok().build();
  }
}
