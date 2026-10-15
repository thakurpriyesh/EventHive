package com.app.eventhive.service;

import com.app.eventhive.dto.AuthResponse;
import com.app.eventhive.dto.LoginRequest;
import com.app.eventhive.dto.RegisterRequest;
import com.app.eventhive.entity.Role;
import com.app.eventhive.entity.User;
import com.app.eventhive.exception.BadRequestException;
import com.app.eventhive.repository.UserRepository;
import com.app.eventhive.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtUtil, authenticationManager);
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest("Alice", "alice@example.com", "secret123", Role.ORGANIZER);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encodedPassword");
        
        User savedUser = User.builder()
                .id(1L)
                .name("Alice")
                .email("alice@example.com")
                .password("encodedPassword")
                .role(Role.ORGANIZER)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("jwt-token-123");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("jwt-token-123", response.token());
        assertEquals(1L, response.userId());
        assertEquals("Alice", response.name());
        assertEquals("alice@example.com", response.email());
        assertEquals(Role.ORGANIZER, response.role());
    }

    @Test
    void testRegisterDuplicateEmailThrowsBadRequest() {
        RegisterRequest request = new RegisterRequest("Bob", "bob@example.com", "secret123", Role.USER);
        when(userRepository.existsByEmail("bob@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterDefaultRoleUser() {
        RegisterRequest request = new RegisterRequest("Charlie", "charlie@example.com", "secret123", null);
        when(userRepository.existsByEmail("charlie@example.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        User savedUser = User.builder().id(2L).name("Charlie").email("charlie@example.com").role(Role.USER).build();
        when(userRepository.save(any())).thenReturn(savedUser);
        when(jwtUtil.generateToken(any())).thenReturn("token");

        AuthResponse response = authService.register(request);
        assertEquals(Role.USER, response.role());
    }

    @Test
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest("alice@example.com", "secret123");
        User user = User.builder().id(1L).name("Alice").email("alice@example.com").role(Role.ORGANIZER).build();

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user)).thenReturn("jwt-token-123");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token-123", response.token());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
