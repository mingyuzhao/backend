package com.toyota.tmmc.service;

import com.toyota.tmmc.dto.AuthRequest;
import com.toyota.tmmc.dto.UserRegistrationRequest;
import com.toyota.tmmc.entity.User;
import com.toyota.tmmc.exception.AuthenticationFailedException;
import com.toyota.tmmc.exception.ResourceConflictException;
import com.toyota.tmmc.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUserWithDefaultRole() {
        User savedUser = User.builder()
                .id(1L)
                .username("alice")
                .password("encoded")
                .role("USER")
                .build();

        when(userService.existsByUsername("Alice")).thenReturn(false);
        when(userService.saveUser(any(User.class))).thenReturn(savedUser);

        User result = authService.register(new UserRegistrationRequest("Alice", "password123"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).saveUser(captor.capture());
        User userToSave = captor.getValue();

        assertEquals("Alice", userToSave.getUsername());
        assertEquals("password123", userToSave.getPassword());
        assertEquals("USER", userToSave.getRole());
        assertEquals(savedUser, result);
    }

    @Test
    void shouldRejectDuplicateUsernameDuringRegistration() {
        when(userService.existsByUsername("alice")).thenReturn(true);

        assertThrows(ResourceConflictException.class,
                () -> authService.register(new UserRegistrationRequest("alice", "password123")));

        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    void shouldReturnTokenWhenLoginSucceeds() {
        User storedUser = User.builder()
                .username("alice")
                .password("encoded-password")
                .role("USER")
                .build();

        when(userService.findByUsername("alice")).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);
        when(jwtUtil.generateToken("alice", "USER")).thenReturn("jwt-token");

        String token = authService.login(new AuthRequest("alice", "password123"));

        assertEquals("jwt-token", token);
    }

    @Test
    void shouldRejectInvalidLogin() {
        when(userService.findByUsername("alice")).thenReturn(Optional.empty());

        assertThrows(AuthenticationFailedException.class,
                () -> authService.login(new AuthRequest("alice", "password123")));

        verify(jwtUtil, never()).generateToken(any(), any());
    }
}


