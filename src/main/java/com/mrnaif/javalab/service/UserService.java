package com.mrnaif.javalab.service;

import com.mrnaif.javalab.dto.PageResponse;
import com.mrnaif.javalab.dto.user.CreateUser;
import com.mrnaif.javalab.dto.user.DisplayUser;
import java.util.Map;
import java.util.Optional;

public interface UserService {

  DisplayUser createUser(CreateUser user);

  PageResponse<DisplayUser> getAllUsers(Integer page, Integer size);

  Optional<DisplayUser> getUserById(Long id);

  DisplayUser updateUser(Long id, CreateUser user);

  DisplayUser partialUpdateUser(Long id, Map<String, Object> updates);

  void deleteUser(Long id);
}