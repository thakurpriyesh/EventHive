package com.app.eventhive.service;

import com.app.eventhive.dto.OrganizerStatsResponse;
import com.app.eventhive.repository.BookingRepository;
import com.app.eventhive.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrganizerService {

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;

    public OrganizerService(EventRepository eventRepository, BookingRepository bookingRepository) {
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
    }

    public OrganizerStatsResponse getOrganizerStats(Long organizerId) {
        long totalEvents = eventRepository.findByOrganizerId(organizerId).size();
        long totalBookings = bookingRepository.countConfirmedBookingsByOrganizerId(organizerId);
        BigDecimal totalRevenue = bookingRepository.getTotalRevenueByOrganizerId(organizerId);

        return new OrganizerStatsResponse(totalEvents, totalBookings, totalRevenue);
    }
}
