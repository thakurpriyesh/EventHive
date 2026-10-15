package com.app.eventhive.service;

import com.app.eventhive.dto.AuthResponse;
import com.app.eventhive.dto.GoogleAuthRequest;
import com.app.eventhive.dto.GoogleUserInfo;
import com.app.eventhive.entity.Role;
import com.app.eventhive.entity.User;
import com.app.eventhive.exception.BadRequestException;
import com.app.eventhive.repository.UserRepository;
import com.app.eventhive.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class GoogleOAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    @Value("${google.client-id:}")
    private String googleClientId;

    @Autowired
    public GoogleOAuthService(UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              JwtUtil jwtUtil) {
        this(userRepository, passwordEncoder, jwtUtil, new RestTemplate());
    }

    public GoogleOAuthService(UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              JwtUtil jwtUtil,
                              RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
    }

    public AuthResponse loginWithGoogle(GoogleAuthRequest request) {
        String token = request != null ? request.getToken() : null;
        if (token == null || token.isBlank()) {
            throw new BadRequestException("Google ID token or credential is required");
        }

        GoogleUserInfo userInfo;
        try {
            userInfo = restTemplate.getForObject(
                    "https://oauth2.googleapis.com/tokeninfo?id_token={token}",
                    GoogleUserInfo.class,
                    token
            );
        } catch (Exception e) {
            throw new BadRequestException("Invalid or expired Google token");
        }

        if (userInfo == null || userInfo.email() == null || !userInfo.isVerified()) {
            throw new BadRequestException("Google account email is not verified");
        }

        if (googleClientId != null && !googleClientId.isBlank() && !googleClientId.equals(userInfo.aud())) {
            throw new BadRequestException("Google token audience mismatch");
        }

        User user = userRepository.findByEmail(userInfo.email())
                .orElseGet(() -> {
                    String name = userInfo.name() != null && !userInfo.name().isBlank()
                            ? userInfo.name()
                            : userInfo.email();
                    User newUser = User.builder()
                            .name(name)
                            .email(userInfo.email())
                            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .role(Role.USER)
                            .build();
                    return userRepository.save(newUser);
                });

        String jwtToken = jwtUtil.generateToken(user);
        return new AuthResponse(jwtToken, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
