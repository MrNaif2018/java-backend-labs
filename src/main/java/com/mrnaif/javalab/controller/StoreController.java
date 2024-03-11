package com.mrnaif.javalab.controller;

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

import com.mrnaif.javalab.model.Product;
import com.mrnaif.javalab.model.Store;
import com.mrnaif.javalab.payload.PageResponse;
import com.mrnaif.javalab.service.StoreService;
import com.mrnaif.javalab.utils.AppConstant;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    public ResponseEntity<Store> createStore(@RequestBody Store store) {
        return ResponseEntity.ok(storeService.createStore(store));
    }

    @GetMapping
    public ResponseEntity<PageResponse<Store>> getAllStores(
            @RequestParam(value = "page", required = false, defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size) {
        return ResponseEntity.ok(storeService.getAllStores(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Store> getStoreById(@PathVariable Long id) {
        // of allows to return 404 if optional is not present()
        return ResponseEntity.of(storeService.getStoreById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Store> updateStore(@PathVariable Long id, @RequestBody Store store) {
        return ResponseEntity.ok(storeService.updateStore(id, store));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Store> partialUpdateStore(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(storeService.partialUpdateStore(id, updates));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/products")
    public ResponseEntity<Store> addProductToStore(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(storeService.addProductToStore(id, product));
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<PageResponse<Product>> getProductsInStore(@PathVariable Long id,
            @RequestParam(value = "page", required = false, defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size) {
        return ResponseEntity.ok(storeService.getProductsInStore(id, page, size));
    }

    @DeleteMapping("/{id}/products/{productId}")
    public ResponseEntity<Void> removeProductFromStore(@PathVariable Long id, @PathVariable Long productId) {
        storeService.removeProductFromStore(id, productId);
        return ResponseEntity.ok().build();
    }

}
