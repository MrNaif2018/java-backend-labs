package com.mrnaif.javalab.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mrnaif.javalab.exception.InvalidRequestException;
import com.mrnaif.javalab.exception.ResourceNotFoundException;
import com.mrnaif.javalab.model.Product;
import com.mrnaif.javalab.model.Store;
import com.mrnaif.javalab.payload.PageResponse;
import com.mrnaif.javalab.repository.ProductRepository;
import com.mrnaif.javalab.repository.StoreRepository;
import com.mrnaif.javalab.service.StoreService;
import com.mrnaif.javalab.utils.AppUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class StoreServiceImpl implements StoreService {
    private StoreRepository storeRepository;
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public StoreServiceImpl(StoreRepository storeRepository, ProductRepository productRepository) {
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
    }

    public Store createStore(Store store) {
        try {
            return storeRepository.save(store);
        } catch (Exception e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }

    public PageResponse<Store> getAllStores(Integer page, Integer size) {
        AppUtils.validatePageAndSize(page, size);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Store> objects = storeRepository.findAll(pageable);
        List<Store> responses = objects.getContent();

        PageResponse<Store> pageResponse = new PageResponse<>();
        pageResponse.setContent(responses);
        pageResponse.setSize(size);
        pageResponse.setPage(page);
        pageResponse.setTotalElements(objects.getNumberOfElements());
        pageResponse.setTotalPages(objects.getTotalPages());
        pageResponse.setLast(objects.isLast());

        return pageResponse;
    }

    public Optional<Store> getStoreById(Long id) {
        return storeRepository.findById(id);
    }

    public Store updateStore(Long id, Store store) {
        store.setId(id); // to allow hibernate to find existing instance
        storeRepository.saveAndFlush(store);
        // required to return proper fields like created unfortunately
        Store managedStore = entityManager.find(Store.class, store.getId());
        entityManager.refresh(managedStore);
        return managedStore;
    }

    public Store partialUpdateStore(Long id, Map<String, Object> updates) {
        Optional<Store> optionalStore = storeRepository.findById(id);
        if (!optionalStore.isPresent()) {
            return null;
        }
        Store store = optionalStore.get();
        if (updates.containsKey("created")) {
            store.setCreated(Instant.parse(updates.remove("created").toString()));
        }
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(store);
        beanWrapper.setPropertyValues(updates);
        storeRepository.saveAndFlush(store);
        entityManager.refresh(store);
        return store;
    }

    public void deleteStore(Long id) {
        storeRepository.deleteById(id);
    }

    public Store addProductToStore(Long storeId, Product product) {
        Optional<Store> optionalStore = storeRepository.findById(storeId);
        if (!optionalStore.isPresent()) {
            return null;
        }
        // save new product if it didn't exist before
        Store store = optionalStore.get();
        store.addProduct(product);
        return storeRepository.save(store);
    }

    public Store removeProductFromStore(Long storeId, Long productId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id = " + storeId));
        store.removeProduct(productId);
        return storeRepository.save(store);
    }

    public PageResponse<Product> getProductsInStore(Long storeId, Integer page, Integer size) {
        AppUtils.validatePageAndSize(page, size);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Product> objects = productRepository.findProductsByStoresId(storeId, pageable);
        List<Product> responses = objects.getContent();

        PageResponse<Product> pageResponse = new PageResponse<>();
        pageResponse.setContent(responses);
        pageResponse.setSize(size);
        pageResponse.setPage(page);
        pageResponse.setTotalElements(objects.getNumberOfElements());
        pageResponse.setTotalPages(objects.getTotalPages());
        pageResponse.setLast(objects.isLast());

        return pageResponse;
    }
}