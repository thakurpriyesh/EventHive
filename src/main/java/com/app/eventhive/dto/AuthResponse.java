package com.app.eventhive.dto;

import com.app.eventhive.entity.Role;

public record AuthResponse(
        String token,
        Long userId,
        String name,
        String email,
        Role role
) {}
