package com.mrnaif.javalab.controller;

import com.mrnaif.javalab.aop.annotation.RequestStats;
import com.mrnaif.javalab.dto.PageResponse;
import com.mrnaif.javalab.dto.user.CreateUser;
import com.mrnaif.javalab.dto.user.DisplayUser;
import com.mrnaif.javalab.service.UserService;
import com.mrnaif.javalab.utils.AppConstant;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequestStats
public class UserController {

  private UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<DisplayUser> createUser(@RequestBody CreateUser user) {
    return ResponseEntity.ok(userService.createUser(user));
  }

  @PostMapping("/bulk")
  public ResponseEntity<List<DisplayUser>> createBulkUsers(@RequestBody List<CreateUser> users) {
    return ResponseEntity.ok(userService.createBulkUsers(users));
  }

  @GetMapping
  public ResponseEntity<PageResponse<DisplayUser>> getAllUsers(
      @RequestParam(
              value = "page",
              required = false,
              defaultValue = AppConstant.DEFAULT_PAGE_NUMBER)
          Integer page,
      @RequestParam(value = "size", required = false, defaultValue = AppConstant.DEFAULT_PAGE_SIZE)
          Integer size) {
    return ResponseEntity.ok(userService.getAllUsers(page, size));
  }

  @GetMapping("/{id}")
  public ResponseEntity<DisplayUser> getUserById(@PathVariable Long id) {
    // of allows to return 404 if optional is not present()
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<DisplayUser> updateUser(
      @PathVariable Long id, @RequestBody CreateUser user) {
    return ResponseEntity.ok(userService.updateUser(id, user));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<DisplayUser> partialUpdateUser(
      @PathVariable Long id, @RequestBody Map<String, Object> updates) {
    return ResponseEntity.ok(userService.partialUpdateUser(id, updates));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.ok().build();
  }
}