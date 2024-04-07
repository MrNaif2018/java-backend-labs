package com.mrnaif.javalab.controller;

import com.mrnaif.javalab.dto.PageResponse;
import com.mrnaif.javalab.dto.product.DisplayProduct;
import com.mrnaif.javalab.dto.store.CreateStore;
import com.mrnaif.javalab.dto.store.DisplayStore;
import com.mrnaif.javalab.dto.store.EditProductsRequest;
import com.mrnaif.javalab.service.StoreService;
import com.mrnaif.javalab.utils.AppConstant;
import java.util.Map;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/stores")
public class StoreController {

  private StoreService storeService;

  public StoreController(StoreService storeService) {
    this.storeService = storeService;
  }

  @PostMapping
  public ResponseEntity<DisplayStore> createStore(@RequestBody CreateStore store) {
    return ResponseEntity.ok(storeService.createStore(store));
  }

  @GetMapping
  public ResponseEntity<PageResponse<DisplayStore>> getAllStores(
      @RequestParam(
              value = "page",
              required = false,
              defaultValue = AppConstant.DEFAULT_PAGE_NUMBER)
          Integer page,
      @RequestParam(value = "size", required = false, defaultValue = AppConstant.DEFAULT_PAGE_SIZE)
          Integer size) {
    return ResponseEntity.ok(storeService.getAllStores(page, size));
  }

  @GetMapping("/{id}")
  public ResponseEntity<DisplayStore> getStoreById(@PathVariable Long id) {
    // of allows to return 404 if optional is not present()
    return ResponseEntity.ok(storeService.getStoreById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<DisplayStore> updateStore(
      @PathVariable Long id, @RequestBody CreateStore store) {
    return ResponseEntity.ok(storeService.updateStore(id, store));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<DisplayStore> partialUpdateStore(
      @PathVariable Long id, @RequestBody Map<String, Object> updates) {
    return ResponseEntity.ok(storeService.partialUpdateStore(id, updates));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
    storeService.deleteStore(id);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{id}/products")
  public ResponseEntity<DisplayStore> addProductToStore(
      @PathVariable Long id, @RequestBody EditProductsRequest request) {
    return ResponseEntity.ok(storeService.addProductToStore(id, request.getProductId()));
  }

  @DeleteMapping("/{id}/products")
  public ResponseEntity<Void> removeProductFromStore(
      @PathVariable Long id, @RequestBody EditProductsRequest request) {
    storeService.removeProductFromStore(id, request.getProductId());
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{id}/products")
  public PageResponse<DisplayProduct> getProductsByStoreIdAndPrice(
      @PathVariable Long id,
      @RequestParam("minPrice") Double minPrice,
      @RequestParam("maxPrice") Double maxPrice,
      @RequestParam(
              value = "page",
              required = false,
              defaultValue = AppConstant.DEFAULT_PAGE_NUMBER)
          Integer page,
      @RequestParam(value = "size", required = false, defaultValue = AppConstant.DEFAULT_PAGE_SIZE)
          Integer size) {
    return storeService.getProductsRange(id, minPrice, maxPrice, page, size);
  }
}
