package com.mrnaif.javalab.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mrnaif.javalab.model.User;
import com.mrnaif.javalab.repository.UserRepository;
import com.mrnaif.javalab.service.UserService;

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

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(Long id, User user) {
        user.setId(id); // to allow hibernate to find existing instance
        userRepository.saveAndFlush(user);
        // required to return proper fields like created unfortunately
        User managedUser = entityManager.find(User.class, user.getId());
        entityManager.refresh(managedUser);
        return managedUser;
    }

    public User partialUpdateUser(Long id, Map<String, Object> updates) {
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
        entityManager.refresh(user);
        return user;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}