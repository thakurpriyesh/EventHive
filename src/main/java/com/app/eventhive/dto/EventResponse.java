package com.app.eventhive.dto;

import com.app.eventhive.entity.EventStatus;

import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String title,
        String description,
        String location,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String imageUrl,
        EventStatus status,
        CategoryResponse category,
        String organizerName,
        LocalDateTime createdAt
) {}
