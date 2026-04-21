package com.toyota.tmmc.controller;

import com.toyota.tmmc.dto.AuthRequest;
import com.toyota.tmmc.dto.AuthResponse;
import com.toyota.tmmc.dto.UserRegistrationRequest;
import com.toyota.tmmc.dto.UserResponse;
import com.toyota.tmmc.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        return ResponseEntity.ok(UserResponse.from(authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(AuthResponse.bearer(authService.login(request)));
    }
}

