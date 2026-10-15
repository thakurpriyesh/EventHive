package com.app.eventhive.service;

import com.app.eventhive.dto.OrganizerStatsResponse;
import com.app.eventhive.entity.Event;
import com.app.eventhive.repository.BookingRepository;
import com.app.eventhive.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizerServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private BookingRepository bookingRepository;

    private OrganizerService organizerService;

    @BeforeEach
    void setUp() {
        organizerService = new OrganizerService(eventRepository, bookingRepository);
    }

    @Test
    void testGetOrganizerStats() {
        Long organizerId = 10L;
        when(eventRepository.findByOrganizerId(organizerId)).thenReturn(List.of(new Event(), new Event()));
        when(bookingRepository.countConfirmedBookingsByOrganizerId(organizerId)).thenReturn(5L);
        when(bookingRepository.getTotalRevenueByOrganizerId(organizerId)).thenReturn(BigDecimal.valueOf(1250.50));

        OrganizerStatsResponse stats = organizerService.getOrganizerStats(organizerId);

        assertNotNull(stats);
        assertEquals(2, stats.totalEvents());
        assertEquals(5, stats.totalBookings());
        assertEquals(BigDecimal.valueOf(1250.50), stats.totalRevenue());
    }
}
