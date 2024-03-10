package com.mrnaif.javalab.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mrnaif.javalab.model.User;

public interface UserService {

    public User createUser(User user);

    public List<User> getAllUsers();

    public Optional<User> getUserById(Long id);

    public User updateUser(Long id, User user);

    public User partialUpdateUser(Long id, Map<String, Object> updates);

    public void deleteUser(Long id);

}