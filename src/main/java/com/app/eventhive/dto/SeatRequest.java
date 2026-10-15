package com.app.eventhive.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SeatRequest(
        @NotBlank String seatNumber,
        @NotNull Long ticketTypeId
) {}
