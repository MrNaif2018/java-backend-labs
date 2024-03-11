package com.mrnaif.javalab.service;

import java.util.Map;
import java.util.Optional;

import com.mrnaif.javalab.model.User;
import com.mrnaif.javalab.payload.PageResponse;

public interface UserService {

    public User createUser(User user);

    public PageResponse<User> getAllUsers(Integer page, Integer size);

    public Optional<User> getUserById(Long id);

    public User updateUser(Long id, User user);

    public User partialUpdateUser(Long id, Map<String, Object> updates);

    public void deleteUser(Long id);

}