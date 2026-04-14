package com.example.FYP.controller;

import com.example.FYP.dto.*;
import com.example.FYP.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.status(201).body(ApiResponse.success(authService.register(registerRequest), 201));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(loginRequest), 200));
    }
}
