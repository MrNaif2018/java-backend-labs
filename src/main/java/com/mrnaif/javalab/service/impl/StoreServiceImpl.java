package com.mrnaif.javalab.service.impl;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
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
import com.mrnaif.javalab.payload.store.CreateStore;
import com.mrnaif.javalab.payload.store.DisplayStore;
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

    ModelMapper modelMapper;

    public StoreServiceImpl(StoreRepository storeRepository, ProductRepository productRepository,
            ModelMapper modelMapper) {
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    public DisplayStore createStore(CreateStore store) {
        try {
            modelMapper.typeMap(CreateStore.class, Store.class).addMappings(mapper -> mapper.skip(Store::setId));
            return modelMapper.map(storeRepository.save(modelMapper.map(store, Store.class)), DisplayStore.class);
        } catch (Exception e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }

    public PageResponse<DisplayStore> getAllStores(Integer page, Integer size) {
        AppUtils.validatePageAndSize(page, size);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Store> objects = storeRepository.findAll(pageable);
        List<DisplayStore> responses = Arrays.asList(modelMapper.map(objects.getContent(), DisplayStore[].class));

        PageResponse<DisplayStore> pageResponse = new PageResponse<>();
        pageResponse.setContent(responses);
        pageResponse.setSize(size);
        pageResponse.setPage(page);
        pageResponse.setTotalElements(objects.getNumberOfElements());
        pageResponse.setTotalPages(objects.getTotalPages());
        pageResponse.setLast(objects.isLast());

        return pageResponse;
    }

    public Optional<DisplayStore> getStoreById(Long id) {
        Optional<Store> store = storeRepository.findById(id);
        if (!store.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(modelMapper.map(store.get(), DisplayStore.class));
    }

    public DisplayStore updateStore(Long id, CreateStore createStore) {
        Store store = modelMapper.map(createStore, Store.class);
        store.setId(id); // to allow hibernate to find existing instance
        storeRepository.saveAndFlush(store);
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
        if (updates.containsKey("created")) {
            store.setCreated(Instant.parse(updates.remove("created").toString()));
        }
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(store);
        beanWrapper.setPropertyValues(updates);
        storeRepository.saveAndFlush(store);
        entityManager.refresh(store);
        return modelMapper.map(store, DisplayStore.class);
    }

    public void deleteStore(Long id) {
        storeRepository.deleteById(id);
    }

    public DisplayStore addProductToStore(Long storeId, Long productId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id = "
                        + storeId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id = "
                        + productId));
        store.addProduct(product);
        Store savedStore = storeRepository.save(store);
        return modelMapper.map(savedStore, DisplayStore.class);
    }

    public DisplayStore removeProductFromStore(Long storeId, Long productId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id = "
                        + storeId));
        store.removeProduct(productId);
        return modelMapper.map(storeRepository.save(store), DisplayStore.class);
    }

}