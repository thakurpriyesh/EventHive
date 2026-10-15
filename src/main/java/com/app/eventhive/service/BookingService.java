package com.app.eventhive.service;

import com.app.eventhive.dto.BookingRequest;
import com.app.eventhive.dto.BookingResponse;
import com.app.eventhive.entity.*;
import com.app.eventhive.exception.BadRequestException;
import com.app.eventhive.exception.ResourceNotFoundException;
import com.app.eventhive.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final BookingSeatRepository bookingSeatRepository;

    public BookingService(BookingRepository bookingRepository,
                          EventRepository eventRepository,
                          TicketTypeRepository ticketTypeRepository,
                          SeatRepository seatRepository,
                          UserRepository userRepository,
                          BookingSeatRepository bookingSeatRepository) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.ticketTypeRepository = ticketTypeRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.bookingSeatRepository = bookingSeatRepository;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request, Long userId) {
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        TicketType ticketType = ticketTypeRepository.findById(request.ticketTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("TicketType not found"));

        if (ticketType.getAvailableQuantity() < request.seatIds().size()) {
            throw new BadRequestException("Not enough available tickets");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Seat> bookedSeats = new ArrayList<>();
        for (Long seatId : request.seatIds()) {
            Seat seat = seatRepository.findByIdAndIsBookedFalse(seatId)
                    .orElseThrow(() -> new BadRequestException("Seat " + seatId + " is not available"));

            // Validate seat belongs to this event and ticket type
            if (!seat.getEvent().getId().equals(event.getId())) {
                throw new BadRequestException("Seat " + seatId + " does not belong to this event");
            }
            if (!seat.getTicketType().getId().equals(ticketType.getId())) {
                throw new BadRequestException("Seat " + seatId + " does not belong to this ticket type");
            }

            seat.setBooked(true);
            seatRepository.save(seat);
            bookedSeats.add(seat);
        }

        ticketType.setAvailableQuantity(ticketType.getAvailableQuantity() - request.seatIds().size());
        ticketTypeRepository.save(ticketType);

        BigDecimal totalAmount = ticketType.getPrice().multiply(BigDecimal.valueOf(request.seatIds().size()));

        Booking booking = Booking.builder()
                .user(user)
                .event(event)
                .ticketType(ticketType)
                .totalAmount(totalAmount)
                .status(BookingStatus.CONFIRMED)
                .build();

        booking = bookingRepository.save(booking);

        List<BookingSeat> bookingSeats = new ArrayList<>();
        for (Seat seat : bookedSeats) {
            bookingSeats.add(BookingSeat.builder()
                    .booking(booking)
                    .seat(seat)
                    .build());
        }
        bookingSeatRepository.saveAll(bookingSeats);
        booking.setBookingSeats(bookingSeats);

        return toBookingResponse(booking);
    }

    public List<BookingResponse> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::toBookingResponse)
                .toList();
    }

    public BookingResponse getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getUser().getId().equals(userId)) {
            throw new BadRequestException("You do not have access to this booking");
        }
        return toBookingResponse(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new BadRequestException("You do not own this booking");
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Booking is not confirmed");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        for (BookingSeat bs : booking.getBookingSeats()) {
            bs.getSeat().setBooked(false);
            seatRepository.save(bs.getSeat());
        }

        TicketType ticketType = booking.getTicketType();
        ticketType.setAvailableQuantity(
                ticketType.getAvailableQuantity() + booking.getBookingSeats().size());
        ticketTypeRepository.save(ticketType);

        bookingRepository.save(booking);
    }

    public List<BookingResponse> getOrganizerBookings(Long organizerId) {
        return bookingRepository.findByEventOrganizerId(organizerId).stream()
                .map(this::toBookingResponse)
                .toList();
    }

    // ---- Helper ----

    private BookingResponse toBookingResponse(Booking booking) {
        List<String> seatNumbers = booking.getBookingSeats().stream()
                .map(bs -> bs.getSeat().getSeatNumber())
                .toList();

        return new BookingResponse(
                booking.getId(),
                booking.getEvent().getTitle(),
                booking.getEvent().getLocation(),
                booking.getTicketType().getName(),
                seatNumbers,
                booking.getTotalAmount(),
                booking.getStatus(),
                booking.getBookingTime()
        );
    }
}
