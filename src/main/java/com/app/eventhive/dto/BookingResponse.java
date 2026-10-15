package com.app.eventhive.dto;

import com.app.eventhive.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long id,
        String eventTitle,
        String eventLocation,
        String ticketTypeName,
        List<String> seatNumbers,
        BigDecimal totalAmount,
        BookingStatus status,
        LocalDateTime bookingTime
) {}
