package com.mrnaif.javalab.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.mrnaif.javalab.dto.BatchDeleteRequest;
import com.mrnaif.javalab.dto.PageResponse;
import com.mrnaif.javalab.dto.user.CreateUser;
import com.mrnaif.javalab.dto.user.DisplayUser;
import com.mrnaif.javalab.model.User;
import com.mrnaif.javalab.service.UserService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
  @Mock private UserService userService;

  @Spy private ModelMapper modelMapper = new ModelMapper();

  @InjectMocks private UserController userController;

  private User user;
  private CreateUser createUser;
  private DisplayUser displayUser;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    user =
        new User(
            1L, "test@test.com", "password", new ArrayList<>(), new ArrayList<>(), Instant.now());
    createUser = modelMapper.map(user, CreateUser.class);
    displayUser = modelMapper.map(user, DisplayUser.class);
  }

  @Test
  void getAllUsers() {
    List<DisplayUser> users = List.of(new DisplayUser(), new DisplayUser(), new DisplayUser());
    PageResponse<DisplayUser> pageResponse =
        new PageResponse<DisplayUser>(users, 3, 1, 10, 1, false);

    when(userService.getAllUsers(1, 10)).thenReturn(pageResponse);

    ResponseEntity<PageResponse<DisplayUser>> response = userController.getAllUsers(1, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(users, response.getBody().getResult());

    verify(userService, times(1)).getAllUsers(1, 10);
    verifyNoMoreInteractions(userService);
  }

  @Test
  void getUserById() {
    Long userId = 1L;

    when(userService.getUserById(userId)).thenReturn(displayUser);

    ResponseEntity<DisplayUser> response = userController.getUserById(userId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(displayUser, response.getBody());

    verify(userService, times(1)).getUserById(userId);
    verifyNoMoreInteractions(userService);
  }

  @Test
  void createUser() {
    when(userService.createUser(createUser)).thenReturn(displayUser);

    ResponseEntity<DisplayUser> response = userController.createUser(createUser);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(displayUser, response.getBody());

    verify(userService, times(1)).createUser(createUser);
    verifyNoMoreInteractions(userService);
  }

  @Test
  void bulkCreateUsers() {
    List<CreateUser> createUsers = List.of(new CreateUser(), new CreateUser(), new CreateUser());
    List<DisplayUser> displayUsers =
        List.of(new DisplayUser(), new DisplayUser(), new DisplayUser());

    when(userService.createBulkUsers(createUsers)).thenReturn(displayUsers);

    ResponseEntity<List<DisplayUser>> response = userController.createBulkUsers(createUsers);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(displayUsers, response.getBody());

    verify(userService, times(1)).createBulkUsers(createUsers);
    verifyNoMoreInteractions(userService);
  }

  @Test
  void updateUser() {
    Long userId = 1L;

    when(userService.updateUser(userId, createUser)).thenReturn(displayUser);

    ResponseEntity<DisplayUser> response = userController.updateUser(userId, createUser);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(displayUser, response.getBody());

    verify(userService, times(1)).updateUser(userId, createUser);
    verifyNoMoreInteractions(userService);
  }

  @Test
  void partialUpdateUser() {
    Long userId = 1L;

    when(userService.partialUpdateUser(userId, new HashMap<>())).thenReturn(displayUser);

    ResponseEntity<DisplayUser> response =
        userController.partialUpdateUser(userId, new HashMap<>());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(displayUser, response.getBody());

    verify(userService, times(1)).partialUpdateUser(userId, new HashMap<>());
    verifyNoMoreInteractions(userService);
  }

  @Test
  void deleteUserById() {
    Long userId = 1L;

    ResponseEntity<Void> response = userController.deleteUser(userId);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    verify(userService, times(1)).deleteUser(userId);
    verifyNoMoreInteractions(userService);
  }

  @Test
  void bulkDeleteUsers() {
    List<Long> userIds = List.of(1L, 2L, 3L);

    ResponseEntity<Void> response = userController.deleteUsers(new BatchDeleteRequest(userIds));

    assertEquals(HttpStatus.OK, response.getStatusCode());

    verify(userService, times(1)).deleteUsers(userIds);
    verifyNoMoreInteractions(userService);
  }
}