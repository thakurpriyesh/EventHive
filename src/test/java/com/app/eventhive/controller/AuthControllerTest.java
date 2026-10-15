package com.app.eventhive.controller;

import com.app.eventhive.dto.AuthResponse;
import com.app.eventhive.dto.GoogleAuthRequest;
import com.app.eventhive.dto.LoginRequest;
import com.app.eventhive.dto.RegisterRequest;
import com.app.eventhive.entity.Role;
import com.app.eventhive.service.AuthService;
import com.app.eventhive.service.GoogleOAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private GoogleOAuthService googleOAuthService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(authService, googleOAuthService);
    }

    @Test
    void testRegister() {
        RegisterRequest request = new RegisterRequest("User", "user@example.com", "pass123", Role.USER);
        AuthResponse response = new AuthResponse("token123", 1L, "User", "user@example.com", Role.USER);
        when(authService.register(request)).thenReturn(response);

        ResponseEntity<AuthResponse> entity = authController.register(request);
        assertEquals(HttpStatus.CREATED, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals("token123", entity.getBody().token());
    }

    @Test
    void testLogin() {
        LoginRequest request = new LoginRequest("user@example.com", "pass123");
        AuthResponse response = new AuthResponse("token123", 1L, "User", "user@example.com", Role.USER);
        when(authService.login(request)).thenReturn(response);

        ResponseEntity<AuthResponse> entity = authController.login(request);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals("token123", entity.getBody().token());
    }

    @Test
    void testGoogleLogin() {
        GoogleAuthRequest request = new GoogleAuthRequest("google-id-token");
        AuthResponse response = new AuthResponse("token-google", 2L, "Google User", "google@example.com", Role.USER);
        when(googleOAuthService.loginWithGoogle(request)).thenReturn(response);

        ResponseEntity<AuthResponse> entity = authController.googleLogin(request);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals("token-google", entity.getBody().token());
        assertEquals("google@example.com", entity.getBody().email());
    }
}
