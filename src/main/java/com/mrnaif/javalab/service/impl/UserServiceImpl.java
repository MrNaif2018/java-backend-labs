package com.mrnaif.javalab.service.impl;

import com.mrnaif.javalab.aop.annotation.Logging;
import com.mrnaif.javalab.dto.PageResponse;
import com.mrnaif.javalab.dto.user.CreateUser;
import com.mrnaif.javalab.dto.user.DisplayUser;
import com.mrnaif.javalab.exception.InvalidRequestException;
import com.mrnaif.javalab.exception.ResourceNotFoundException;
import com.mrnaif.javalab.model.User;
import com.mrnaif.javalab.repository.UserRepository;
import com.mrnaif.javalab.service.UserService;
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
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Logging
public class UserServiceImpl implements UserService {
  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;

  @PersistenceContext private EntityManager entityManager;

  ModelMapper modelMapper;

  GenericCache<Long, User> cache;

  public UserServiceImpl(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      ModelMapper modelMapper,
      CacheFactory cacheFactory) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.modelMapper = modelMapper;
    this.cache = cacheFactory.getCache(User.class);
  }

  public DisplayUser createUser(CreateUser user) {
    try {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      return modelMapper.map(
          userRepository.save(modelMapper.map(user, User.class)), DisplayUser.class);
    } catch (Exception e) {
      throw new InvalidRequestException(e.getMessage());
    }
  }

  public PageResponse<DisplayUser> getAllUsers(Integer page, Integer size) {
    AppUtils.validatePageAndSize(page, size);
    Pageable pageable = PageRequest.of(page - 1, size);
    Page<User> objects = userRepository.findAll(pageable);
    List<DisplayUser> responses =
        Arrays.asList(modelMapper.map(objects.getContent(), DisplayUser[].class));

    PageResponse<DisplayUser> pageResponse = new PageResponse<>();
    pageResponse.setContent(responses);
    pageResponse.setSize(size);
    pageResponse.setPage(page);
    pageResponse.setTotalElements(objects.getNumberOfElements());
    pageResponse.setTotalPages(objects.getTotalPages());
    pageResponse.setLast(objects.isLast());

    return pageResponse;
  }

  public DisplayUser getUserById(Long id) {
    User user =
        cache
            .get(id)
            .orElseGet(
                () ->
                    userRepository
                        .findById(id)
                        .orElseThrow(
                            () -> new ResourceNotFoundException("User not found with id = " + id)));
    cache.put(id, user);
    return modelMapper.map(user, DisplayUser.class);
  }

  public DisplayUser updateUser(Long id, CreateUser createUser) {
    User user = modelMapper.map(createUser, User.class);
    user.setId(id); // to allow hibernate to find existing instance
    try {
      userRepository.saveAndFlush(user);
    } catch (Exception e) {
      throw new InvalidRequestException(e.getMessage());
    }
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
    try {
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
    } catch (Exception e) {
      throw new InvalidRequestException(e.getMessage());
    }
    return modelMapper.map(user, DisplayUser.class);
  }

  public void deleteUser(Long id) {
    userRepository.deleteById(id);
    cache.invalidate(id);
  }
}