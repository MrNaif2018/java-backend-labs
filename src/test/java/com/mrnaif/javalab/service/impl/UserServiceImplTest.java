package com.mrnaif.javalab.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.mrnaif.javalab.dto.PageResponse;
import com.mrnaif.javalab.dto.user.CreateUser;
import com.mrnaif.javalab.dto.user.DisplayUser;
import com.mrnaif.javalab.exception.InvalidRequestException;
import com.mrnaif.javalab.exception.ResourceNotFoundException;
import com.mrnaif.javalab.model.User;
import com.mrnaif.javalab.repository.UserRepository;
import com.mrnaif.javalab.utils.cache.CacheFactory;
import com.mrnaif.javalab.utils.cache.GenericCache;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
  @Mock private CacheFactory cacheFactory;

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private EntityManager entityManager;

  @Spy private ModelMapper modelMapper = new ModelMapper();

  @Mock private GenericCache<Long, User> cache;

  @InjectMocks private UserServiceImpl userService;

  private User user;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    user = new User(1L, "test@test.com", passwordEncoder.encode("password"), Instant.now());
  }

  @Test
  void testCreateUser() {
    assertThrows(InvalidRequestException.class, () -> userService.createUser(null));
    when(cache.get(1L)).thenReturn(Optional.empty());
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    DisplayUser gotUser = userService.getUserById(1L);

    assertEquals(modelMapper.map(user, DisplayUser.class), gotUser);
    verify(cache, times(1)).put(1L, user);
  }

  @Test
  void testGetUserByIdCacheHit() {
    when(cache.get(1L)).thenReturn(Optional.of(user));

    DisplayUser gotUser = userService.getUserById(1L);

    assertEquals(modelMapper.map(user, DisplayUser.class), gotUser);
    verify(userRepository, never()).findById(anyLong());
    verify(cache, times(1)).put(1L, user);
  }

  @Test
  void testGetUserByIdCacheMiss() {
    when(cache.get(1L)).thenReturn(Optional.empty());
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    DisplayUser gotUser = userService.getUserById(1L);

    assertEquals(modelMapper.map(user, DisplayUser.class), gotUser);
    verify(cache).put(1L, user);
  }

  @Test
  void testGetUserByIdNotFound() {
    when(cache.get(1L)).thenReturn(Optional.empty());
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    verify(cache, never()).put(anyLong(), any(User.class));
  }

  @Test
  void testGetAllUsers() {
    List<User> users = List.of(user, user, user);
    Page<User> pagedUsers = new PageImpl<User>(users);
    when(userRepository.findAll(any(Pageable.class))).thenReturn(pagedUsers);
    PageResponse<DisplayUser> response = userService.getAllUsers(1, 10);
    List<DisplayUser> result = response.getContent();

    assertEquals(1, response.getPage());
    assertEquals(10, response.getSize());
    assertEquals(3, response.getTotalElements());
    assertEquals(3, result.size());
    assertEquals(modelMapper.map(user, DisplayUser.class), result.get(1));
  }

  @Test
  void testSaveUser() {
    when(userRepository.save(any(User.class))).thenReturn(user);

    DisplayUser saved =
        modelMapper.map(
            userService.createUser(modelMapper.map(user, CreateUser.class)), DisplayUser.class);

    assertNotNull(saved);
    assertEquals(modelMapper.map(user, DisplayUser.class), saved);
  }

  @Test
  void testUpdateUser() {
    when(entityManager.find(User.class, 1L)).thenReturn(user);
    DisplayUser updated =
        modelMapper.map(
            userService.updateUser(user.getId(), modelMapper.map(user, CreateUser.class)),
            DisplayUser.class);

    assertNotNull(updated);
    assertEquals(modelMapper.map(user, DisplayUser.class), updated);
    verify(cache, times(1)).invalidate(1L);
  }

  @Test
  void testPartialUpdateUser() {
    assertNull(userService.partialUpdateUser(1L, new HashMap<>()));
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    Instant now = Instant.now();
    DisplayUser updated =
        modelMapper.map(
            userService.partialUpdateUser(
                user.getId(),
                new HashMap<>() {
                  {
                    put("email", "test2@test.com");
                    put("password", "12345678");
                    put("created", now);
                  }
                }),
            DisplayUser.class);

    assertNotNull(updated);
    assertEquals("test2@test.com", updated.getEmail());
    assertEquals(now, updated.getCreated());
    verify(cache, times(1)).invalidate(1L);
  }

  @Test
  void testBulkAddUsers() {
    assertThrows(InvalidRequestException.class, () -> userService.createBulkUsers(null));

    List<CreateUser> users = List.of(modelMapper.map(user, CreateUser.class));
    when(userRepository.save(any(User.class))).thenReturn(user);

    List<DisplayUser> savedUsers = userService.createBulkUsers(users);

    assertEquals(1, savedUsers.size());
    assertEquals(modelMapper.map(user, DisplayUser.class), savedUsers.get(0));
  }

  @Test
  void testDeleteUser() {
    doNothing().when(userRepository).deleteById(1L);
    userService.deleteUser(1L);
    verify(userRepository, times(1)).deleteById(1L);
    verify(cache, times(1)).invalidate(1L);
  }
}
