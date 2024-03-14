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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mrnaif.javalab.exception.InvalidRequestException;
import com.mrnaif.javalab.model.User;
import com.mrnaif.javalab.payload.PageResponse;
import com.mrnaif.javalab.payload.user.CreateUser;
import com.mrnaif.javalab.payload.user.DisplayUser;
import com.mrnaif.javalab.repository.UserRepository;
import com.mrnaif.javalab.service.UserService;
import com.mrnaif.javalab.utils.AppUtils;
import com.mrnaif.javalab.utils.cache.GenericCache;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    ModelMapper modelMapper;

    GenericCache<Long, User> cache;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper,
            GenericCache<Long, User> cache) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.cache = cache;
    }

    public DisplayUser createUser(CreateUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            return modelMapper.map(userRepository.save(modelMapper.map(user, User.class)), DisplayUser.class);
        } catch (Exception e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }

    public PageResponse<DisplayUser> getAllUsers(Integer page, Integer size) {
        AppUtils.validatePageAndSize(page, size);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> objects = userRepository.findAll(pageable);
        List<DisplayUser> responses = Arrays.asList(modelMapper.map(objects.getContent(), DisplayUser[].class));

        PageResponse<DisplayUser> pageResponse = new PageResponse<>();
        pageResponse.setContent(responses);
        pageResponse.setSize(size);
        pageResponse.setPage(page);
        pageResponse.setTotalElements(objects.getNumberOfElements());
        pageResponse.setTotalPages(objects.getTotalPages());
        pageResponse.setLast(objects.isLast());

        return pageResponse;
    }

    public Optional<DisplayUser> getUserById(Long id) {
        User user = cache.get(id).orElseGet(() -> userRepository.findById(id).orElse(null));
        if (user == null) {
            return Optional.empty();
        }
        cache.put(id, user);
        return Optional.of(modelMapper.map(user, DisplayUser.class));
    }

    public DisplayUser updateUser(Long id, CreateUser createUser) {
        User user = modelMapper.map(createUser, User.class);
        user.setId(id); // to allow hibernate to find existing instance
        userRepository.saveAndFlush(user);
        cache.invalidate(id);
        // required to return proper fields like created unfortunately
        User managedUser = entityManager.find(User.class, user.getId());
        entityManager.refresh(managedUser);
        return modelMapper.map(managedUser, DisplayUser.class);
    }

    public DisplayUser partialUpdateUser(Long id, Map<String, Object> updates) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return null;
        }
        User user = optionalUser.get();
        if (updates.containsKey("created")) {
            user.setCreated(Instant.parse(updates.remove("created").toString()));
        }
        if (updates.containsKey("password")) {
            user.setPassword(passwordEncoder.encode(updates.remove("password").toString()));
        }
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(user);
        beanWrapper.setPropertyValues(updates);
        userRepository.saveAndFlush(user);
        cache.invalidate(id);
        entityManager.refresh(user);
        return modelMapper.map(user, DisplayUser.class);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        cache.invalidate(id);
    }

}