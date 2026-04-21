package com.toyota.tmmc.service;

import com.toyota.tmmc.dto.AuthRequest;
import com.toyota.tmmc.dto.UserRegistrationRequest;
import com.toyota.tmmc.entity.User;
import com.toyota.tmmc.exception.AuthenticationFailedException;
import com.toyota.tmmc.exception.ResourceConflictException;
import com.toyota.tmmc.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String login(AuthRequest request) {
        Optional<User> userOpt = userService.findByUsername(request.username());
        if (userOpt.isPresent() && passwordEncoder.matches(request.password(), userOpt.get().getPassword())) {
            User user = userOpt.get();
            return jwtUtil.generateToken(user.getUsername(), user.getRole());
        }
        throw new AuthenticationFailedException("Invalid username or password");
    }

    public User register(UserRegistrationRequest request) {
        if (userService.existsByUsername(request.username())) {
            throw new ResourceConflictException("Username already exists");
        }

        User user = User.builder()
                .username(request.username())
                .password(request.password())
                .role("USER")
                .build();

        return userService.saveUser(user);
    }
}

