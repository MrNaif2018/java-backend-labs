package com.mrnaif.javalab.service;

import java.util.Map;
import java.util.Optional;

import com.mrnaif.javalab.payload.PageResponse;
import com.mrnaif.javalab.payload.user.CreateUser;
import com.mrnaif.javalab.payload.user.DisplayUser;

public interface UserService {

    public DisplayUser createUser(CreateUser user);

    public PageResponse<DisplayUser> getAllUsers(Integer page, Integer size);

    public Optional<DisplayUser> getUserById(Long id);

    public DisplayUser updateUser(Long id, CreateUser user);

    public DisplayUser partialUpdateUser(Long id, Map<String, Object> updates);

    public void deleteUser(Long id);

}