package com.lets_play.lets_play.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.lets_play.lets_play.dto.UserRegistrationRequest;
import com.lets_play.lets_play.dto.UserResponse;
import com.lets_play.lets_play.service.UserService;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

   @Autowired
   private UserService userService;

   @GetMapping
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<List<UserResponse>> getAllUsers() {
      List<UserResponse> users = userService.getAllUsers();
      return ResponseEntity.ok(users);
   }

   @GetMapping("/{id}")
   @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#id, authentication.name)")
   @PostAuthorize("hasRole('ADMIN') or returnObject.body.email == authentication.name")
   public ResponseEntity<UserResponse> getUserById(@PathVariable String id, Authentication authentication) {
      UserResponse user = userService.getUserById(id);
      return ResponseEntity.ok(user);
   }

   @PutMapping("/{id}")
   @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#id, authentication.name)")
   public ResponseEntity<UserResponse> updateUser(
         @PathVariable String id,
         @Valid @RequestBody UserRegistrationRequest request,
         Authentication authentication) {

      UserResponse updatedUser = userService.updateUser(id, request);
      return ResponseEntity.ok(updatedUser);
   }

   @DeleteMapping("/{id}")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<?> deleteUser(@PathVariable String id) {
      userService.deleteUser(id);
      Map<String, String> response = new HashMap<>();
      response.put("message", "User deleted successfully");
      return ResponseEntity.ok(response);
   }

   @GetMapping("/{id}/products")
   @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#id, authentication.name)")
   public ResponseEntity<?> getUserProducts(@PathVariable String id) {
      // This will be implemented when we create ProductService method
      Map<String, String> response = new HashMap<>();
      response.put("message", "Get user products - to be implemented with ProductController");
      return ResponseEntity.ok(response);
   }
}
