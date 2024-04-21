package com.mrnaif.javalab.service;

import com.mrnaif.javalab.dto.PageResponse;
import com.mrnaif.javalab.dto.user.CreateUser;
import com.mrnaif.javalab.dto.user.DisplayUser;
import java.util.List;
import java.util.Map;

public interface UserService {

  DisplayUser createUser(CreateUser user);

  List<DisplayUser> createBulkUsers(List<CreateUser> users);

  PageResponse<DisplayUser> getAllUsers(Integer page, Integer size);

  DisplayUser getUserById(Long id);

  DisplayUser updateUser(Long id, CreateUser user);

  DisplayUser partialUpdateUser(Long id, Map<String, Object> updates);

  void deleteUser(Long id);
}