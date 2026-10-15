package com.app.eventhive.service;

import com.app.eventhive.dto.AuthResponse;
import com.app.eventhive.dto.GoogleAuthRequest;
import com.app.eventhive.dto.GoogleUserInfo;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleOAuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RestTemplate restTemplate;

    private GoogleOAuthService googleOAuthService;

    @BeforeEach
    void setUp() {
        googleOAuthService = new GoogleOAuthService(userRepository, passwordEncoder, jwtUtil, restTemplate);
    }

    @Test
    void testLoginWithGoogleExistingUser() {
        GoogleAuthRequest request = new GoogleAuthRequest("valid-google-id-token");
        GoogleUserInfo userInfo = new GoogleUserInfo("john@gmail.com", true, "John Doe", "sub-123", "client-id-xyz", "avatar.png");

        when(restTemplate.getForObject(anyString(), eq(GoogleUserInfo.class), eq("valid-google-id-token")))
                .thenReturn(userInfo);

        User existingUser = User.builder()
                .id(5L)
                .name("John Doe")
                .email("john@gmail.com")
                .role(Role.USER)
                .build();
        when(userRepository.findByEmail("john@gmail.com")).thenReturn(Optional.of(existingUser));
        when(jwtUtil.generateToken(existingUser)).thenReturn("app-jwt-token");

        AuthResponse response = googleOAuthService.loginWithGoogle(request);

        assertNotNull(response);
        assertEquals("app-jwt-token", response.token());
        assertEquals("john@gmail.com", response.email());
        assertEquals(5L, response.userId());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testLoginWithGoogleNewUserAutoRegistration() {
        GoogleAuthRequest request = new GoogleAuthRequest("new-user-id-token");
        GoogleUserInfo userInfo = new GoogleUserInfo("new@gmail.com", "true", "New User", "sub-456", "client-id-xyz", null);

        when(restTemplate.getForObject(anyString(), eq(GoogleUserInfo.class), eq("new-user-id-token")))
                .thenReturn(userInfo);

        when(userRepository.findByEmail("new@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-random-password");

        User savedUser = User.builder()
                .id(10L)
                .name("New User")
                .email("new@gmail.com")
                .role(Role.USER)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(savedUser)).thenReturn("new-user-jwt");

        AuthResponse response = googleOAuthService.loginWithGoogle(request);

        assertNotNull(response);
        assertEquals(10L, response.userId());
        assertEquals("new@gmail.com", response.email());
        assertEquals("new-user-jwt", response.token());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testLoginWithGoogleMissingTokenThrows() {
        GoogleAuthRequest emptyRequest = new GoogleAuthRequest(null);
        assertThrows(BadRequestException.class, () -> googleOAuthService.loginWithGoogle(emptyRequest));
    }

    @Test
    void testLoginWithGoogleInvalidTokenThrows() {
        GoogleAuthRequest request = new GoogleAuthRequest("invalid-token");
        when(restTemplate.getForObject(anyString(), eq(GoogleUserInfo.class), eq("invalid-token")))
                .thenThrow(new RestClientException("400 Bad Request"));

        assertThrows(BadRequestException.class, () -> googleOAuthService.loginWithGoogle(request));
    }

    @Test
    void testLoginWithGoogleUnverifiedEmailThrows() {
        GoogleAuthRequest request = new GoogleAuthRequest("unverified-token");
        GoogleUserInfo userInfo = new GoogleUserInfo("unverified@gmail.com", false, "Unverified User", "sub-789", "aud", null);

        when(restTemplate.getForObject(anyString(), eq(GoogleUserInfo.class), eq("unverified-token")))
                .thenReturn(userInfo);

        assertThrows(BadRequestException.class, () -> googleOAuthService.loginWithGoogle(request));
    }

    @Test
    void testLoginWithGoogleAudienceMismatchThrows() {
        GoogleAuthRequest request = new GoogleAuthRequest("token-aud-mismatch");
        GoogleUserInfo userInfo = new GoogleUserInfo("user@gmail.com", true, "User", "sub-000", "wrong-aud", null);

        ReflectionTestUtils.setField(googleOAuthService, "googleClientId", "expected-aud");

        when(restTemplate.getForObject(anyString(), eq(GoogleUserInfo.class), eq("token-aud-mismatch")))
                .thenReturn(userInfo);

        assertThrows(BadRequestException.class, () -> googleOAuthService.loginWithGoogle(request));
    }
}
