package com.app.eventhive.dto;

import java.math.BigDecimal;

public record OrganizerStatsResponse(
        long totalEvents,
        long totalBookings,
        BigDecimal totalRevenue
) {}
