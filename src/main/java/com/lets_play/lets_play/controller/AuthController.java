package com.lets_play.lets_play.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.security.PermitAll;

import com.lets_play.lets_play.dto.LoginRequest;
import com.lets_play.lets_play.dto.UserRegistrationRequest;
import com.lets_play.lets_play.dto.UserResponse;
import com.lets_play.lets_play.security.JwtUtils;
import com.lets_play.lets_play.security.UserDetailsImpl;
import com.lets_play.lets_play.service.UserService;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    @PermitAll
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("type", "Bearer");
        response.put("id", userDetails.getId());
        response.put("email", userDetails.getEmail());
        response.put("name", userDetails.getName());
        response.put("roles", roles);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @PermitAll
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest signUpRequest) {
        Map<String, String> response = new HashMap<>();

        try {
            UserResponse userResponse = userService.createUser(signUpRequest);
            response.put("message", "User registered successfully!");
            response.put("userId", userResponse.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getUserById(userDetails.getId()));
    }

    @GetMapping("/test")
    @PermitAll
    public ResponseEntity<?> testAuth() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Authentication endpoints are working!");
        return ResponseEntity.ok(response);
    }
}
