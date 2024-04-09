package com.mrnaif.javalab.service.impl;

import com.mrnaif.javalab.aop.annotation.Logging;
import com.mrnaif.javalab.dto.PageResponse;
import com.mrnaif.javalab.dto.product.DisplayProduct;
import com.mrnaif.javalab.dto.store.CreateStore;
import com.mrnaif.javalab.dto.store.DisplayStore;
import com.mrnaif.javalab.exception.InvalidRequestException;
import com.mrnaif.javalab.exception.ResourceNotFoundException;
import com.mrnaif.javalab.model.Product;
import com.mrnaif.javalab.model.Store;
import com.mrnaif.javalab.repository.ProductRepository;
import com.mrnaif.javalab.repository.StoreRepository;
import com.mrnaif.javalab.service.StoreService;
import com.mrnaif.javalab.utils.AppConstant;
import com.mrnaif.javalab.utils.AppUtils;
import com.mrnaif.javalab.utils.cache.CacheFactory;
import com.mrnaif.javalab.utils.cache.GenericCache;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Logging
public class StoreServiceImpl implements StoreService {
  private StoreRepository storeRepository;
  private ProductRepository productRepository;

  @PersistenceContext private EntityManager entityManager;

  ModelMapper modelMapper;

  GenericCache<Long, Store> cache;
  GenericCache<Long, Product> productCache;

  public StoreServiceImpl(
      StoreRepository storeRepository,
      ProductRepository productRepository,
      ModelMapper modelMapper,
      CacheFactory cacheFactory) {
    this.storeRepository = storeRepository;
    this.productRepository = productRepository;
    this.modelMapper = modelMapper;
    this.cache = cacheFactory.getCache(Store.class);
    this.productCache = cacheFactory.getCache(Product.class);
  }

  public DisplayStore createStore(CreateStore store) {
    try {
      return modelMapper.map(
          storeRepository.save(modelMapper.map(store, Store.class)), DisplayStore.class);
    } catch (Exception e) {
      throw new InvalidRequestException(e.getMessage());
    }
  }

  public PageResponse<DisplayStore> getAllStores(Integer page, Integer size) {
    AppUtils.validatePageAndSize(page, size);
    Pageable pageable = PageRequest.of(page - 1, size);
    Page<Store> objects = storeRepository.findAll(pageable);
    List<DisplayStore> responses =
        Arrays.asList(modelMapper.map(objects.getContent(), DisplayStore[].class));

    PageResponse<DisplayStore> pageResponse = new PageResponse<>();
    pageResponse.setContent(responses);
    pageResponse.setSize(size);
    pageResponse.setPage(page);
    pageResponse.setTotalElements(objects.getNumberOfElements());
    pageResponse.setTotalPages(objects.getTotalPages());
    pageResponse.setLast(objects.isLast());

    return pageResponse;
  }

  public DisplayStore getStoreById(Long id) {
    Store store =
        cache
            .get(id)
            .orElseGet(
                () ->
                    storeRepository
                        .findById(id)
                        .orElseThrow(
                            () -> new ResourceNotFoundException(AppConstant.STORE_NOT_FOUND + id)));
    cache.put(id, store);
    return modelMapper.map(store, DisplayStore.class);
  }

  public DisplayStore updateStore(Long id, CreateStore createStore) {
    Store store = modelMapper.map(createStore, Store.class);
    store.setId(id); // to allow hibernate to find existing instance
    try {
      storeRepository.saveAndFlush(store);
    } catch (Exception e) {
      throw new InvalidRequestException(e.getMessage());
    }
    cache.invalidate(id);
    // required to return proper fields like created unfortunately
    Store managedStore = entityManager.find(Store.class, store.getId());
    entityManager.refresh(managedStore);
    return modelMapper.map(managedStore, DisplayStore.class);
  }

  public DisplayStore partialUpdateStore(Long id, Map<String, Object> updates) {
    Optional<Store> optionalStore = storeRepository.findById(id);
    if (!optionalStore.isPresent()) {
      return null;
    }
    Store store = optionalStore.get();
    try {
      if (updates.containsKey("created")) {
        store.setCreated(Instant.parse(updates.remove("created").toString()));
      }
      BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(store);
      beanWrapper.setPropertyValues(updates);
      storeRepository.saveAndFlush(store);
      cache.invalidate(id);
      entityManager.refresh(store);
    } catch (Exception e) {
      throw new InvalidRequestException(e.getMessage());
    }
    return modelMapper.map(store, DisplayStore.class);
  }

  public void deleteStore(Long id) {
    Store store =
        storeRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(AppConstant.STORE_NOT_FOUND + id));
    Set.copyOf(store.getProducts())
        .forEach(
            product -> {
              product.removeStore(id);
              productCache.invalidate(product.getId());
            });
    storeRepository.deleteById(id);
    cache.invalidate(id);
  }

  public DisplayStore addProductToStore(Long storeId, Long productId) {
    Store store =
        storeRepository
            .findById(storeId)
            .orElseThrow(
                () -> new ResourceNotFoundException(AppConstant.STORE_NOT_FOUND + storeId));
    Product product;
    try {
      product =
          productRepository
              .findById(productId)
              .orElseThrow(
                  () -> new ResourceNotFoundException("Product not found with id = " + productId));
    } catch (Exception e) {
      throw new InvalidRequestException(e.getMessage());
    }
    store.addProduct(product);
    Store savedStore = storeRepository.save(store);
    cache.invalidate(storeId);
    productCache.invalidate(productId);
    return modelMapper.map(savedStore, DisplayStore.class);
  }

  public DisplayStore removeProductFromStore(Long storeId, Long productId) {
    Store store =
        storeRepository
            .findById(storeId)
            .orElseThrow(
                () -> new ResourceNotFoundException(AppConstant.STORE_NOT_FOUND + storeId));
    store.removeProduct(productId);
    cache.invalidate(storeId);
    productCache.invalidate(productId);
    return modelMapper.map(storeRepository.save(store), DisplayStore.class);
  }

  public PageResponse<DisplayProduct> getProductsRange(
      Long storeId, Double minPrice, Double maxPrice, Integer page, Integer size) {
    AppUtils.validatePageAndSize(page, size);
    Pageable pageable = PageRequest.of(page - 1, size);
    Page<Product> objects =
        productRepository.findProductsByStoresIdAndPrice(storeId, minPrice, maxPrice, pageable);
    List<DisplayProduct> responses =
        Arrays.asList(modelMapper.map(objects.getContent(), DisplayProduct[].class));

    PageResponse<DisplayProduct> pageResponse = new PageResponse<>();
    pageResponse.setContent(responses);
    pageResponse.setSize(size);
    pageResponse.setPage(page);
    pageResponse.setTotalElements(objects.getNumberOfElements());
    pageResponse.setTotalPages(objects.getTotalPages());
    pageResponse.setLast(objects.isLast());

    return pageResponse;
  }
}