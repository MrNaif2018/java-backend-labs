package com.mrnaif.javalab.service;

import java.util.Map;
import java.util.Optional;

import com.mrnaif.javalab.payload.PageResponse;
import com.mrnaif.javalab.payload.user.CreateUser;
import com.mrnaif.javalab.payload.user.DisplayUser;

public interface UserService {

    DisplayUser createUser(CreateUser user);

    PageResponse<DisplayUser> getAllUsers(Integer page, Integer size);

    Optional<DisplayUser> getUserById(Long id);

    DisplayUser updateUser(Long id, CreateUser user);

    DisplayUser partialUpdateUser(Long id, Map<String, Object> updates);

    void deleteUser(Long id);

}