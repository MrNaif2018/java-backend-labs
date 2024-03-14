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
import com.mrnaif.javalab.model.Product;
import com.mrnaif.javalab.payload.PageResponse;
import com.mrnaif.javalab.payload.product.CreateProduct;
import com.mrnaif.javalab.payload.product.DisplayProduct;
import com.mrnaif.javalab.repository.ProductRepository;
import com.mrnaif.javalab.service.ProductService;
import com.mrnaif.javalab.utils.AppUtils;
import com.mrnaif.javalab.utils.cache.GenericCache;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    ModelMapper modelMapper;

    GenericCache<Long, Product> cache;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper,
            GenericCache<Long, Product> cache) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.cache = cache;
    }

    public DisplayProduct createProduct(CreateProduct product) {
        try {
            // required because of id fields
            modelMapper.typeMap(CreateProduct.class, Product.class).addMappings(mapper -> mapper.skip(Product::setId));
            return modelMapper.map(productRepository.save(modelMapper.map(product, Product.class)),
                    DisplayProduct.class);
        } catch (Exception e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }

    public PageResponse<DisplayProduct> getAllProducts(Integer page, Integer size) {
        AppUtils.validatePageAndSize(page, size);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Product> objects = productRepository.findAll(pageable);
        List<DisplayProduct> responses = Arrays.asList(modelMapper.map(objects.getContent(), DisplayProduct[].class));

        PageResponse<DisplayProduct> pageResponse = new PageResponse<>();
        pageResponse.setContent(responses);
        pageResponse.setSize(size);
        pageResponse.setPage(page);
        pageResponse.setTotalElements(objects.getNumberOfElements());
        pageResponse.setTotalPages(objects.getTotalPages());
        pageResponse.setLast(objects.isLast());

        return pageResponse;
    }

    public Optional<DisplayProduct> getProductById(Long id) {
        Product product = cache.get(id).orElseGet(() -> productRepository.findById(id).orElse(null));
        if (product == null) {
            return Optional.empty();
        }
        cache.put(id, product);
        return Optional.of(modelMapper.map(product, DisplayProduct.class));
    }

    public DisplayProduct updateProduct(Long id, CreateProduct createProduct) {
        Product product = modelMapper.map(createProduct, Product.class);
        product.setId(id); // to allow hibernate to find existing instance
        productRepository.saveAndFlush(product);
        cache.invalidate(id);
        // required to return proper fields like created unfortunately
        Product managedProduct = entityManager.find(Product.class, product.getId());
        entityManager.refresh(managedProduct);
        return modelMapper.map(managedProduct, DisplayProduct.class);
    }

    public DisplayProduct partialUpdateProduct(Long id, Map<String, Object> updates) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            return null;
        }
        Product product = optionalProduct.get();
        if (updates.containsKey("created")) {
            product.setCreated(Instant.parse(updates.remove("created").toString()));
        }
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(product);
        beanWrapper.setPropertyValues(updates);
        productRepository.saveAndFlush(product);
        cache.invalidate(id);
        entityManager.refresh(product);
        return modelMapper.map(product, DisplayProduct.class);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
        cache.invalidate(id);
    }
}