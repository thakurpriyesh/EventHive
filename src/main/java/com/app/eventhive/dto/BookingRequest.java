package com.app.eventhive.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookingRequest(
        @NotNull Long eventId,
        @NotNull Long ticketTypeId,
        @NotEmpty List<Long> seatIds
) {}
