package com.app.eventhive.service;

import com.app.eventhive.dto.BookingRequest;
import com.app.eventhive.dto.BookingResponse;
import com.app.eventhive.entity.*;
import com.app.eventhive.exception.BadRequestException;
import com.app.eventhive.exception.ResourceNotFoundException;
import com.app.eventhive.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private TicketTypeRepository ticketTypeRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingSeatRepository bookingSeatRepository;

    private BookingService bookingService;

    private User user;
    private Event event;
    private TicketType ticketType;
    private Seat seat1;
    private Seat seat2;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, eventRepository, ticketTypeRepository,
                seatRepository, userRepository, bookingSeatRepository);

        user = User.builder().id(1L).name("Customer").email("cust@example.com").role(Role.USER).build();
        event = Event.builder().id(10L).title("Concert").location("Stadium").build();
        ticketType = TicketType.builder()
                .id(20L)
                .name("Standard")
                .price(BigDecimal.valueOf(50))
                .totalQuantity(100)
                .availableQuantity(10)
                .event(event)
                .build();
        seat1 = Seat.builder().id(101L).seatNumber("A1").isBooked(false).event(event).ticketType(ticketType).build();
        seat2 = Seat.builder().id(102L).seatNumber("A2").isBooked(false).event(event).ticketType(ticketType).build();
    }

    @Test
    void testCreateBookingSuccess() {
        BookingRequest request = new BookingRequest(10L, 20L, List.of(101L, 102L));

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(ticketTypeRepository.findById(20L)).thenReturn(Optional.of(ticketType));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(seatRepository.findByIdAndIsBookedFalse(101L)).thenReturn(Optional.of(seat1));
        when(seatRepository.findByIdAndIsBookedFalse(102L)).thenReturn(Optional.of(seat2));

        Booking savedBooking = Booking.builder()
                .id(500L)
                .user(user)
                .event(event)
                .ticketType(ticketType)
                .totalAmount(BigDecimal.valueOf(100))
                .status(BookingStatus.CONFIRMED)
                .bookingTime(LocalDateTime.now())
                .bookingSeats(new ArrayList<>())
                .build();
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        BookingResponse response = bookingService.createBooking(request, 1L);

        assertNotNull(response);
        assertEquals(500L, response.id());
        assertEquals("Concert", response.eventTitle());
        assertEquals(BigDecimal.valueOf(100), response.totalAmount());
        assertTrue(seat1.isBooked());
        assertTrue(seat2.isBooked());
        assertEquals(8, ticketType.getAvailableQuantity());
        verify(bookingSeatRepository).saveAll(any());
    }

    @Test
    void testCreateBookingNotEnoughTicketsThrows() {
        ticketType.setAvailableQuantity(1);
        BookingRequest request = new BookingRequest(10L, 20L, List.of(101L, 102L));

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(ticketTypeRepository.findById(20L)).thenReturn(Optional.of(ticketType));

        assertThrows(BadRequestException.class, () -> bookingService.createBooking(request, 1L));
    }

    @Test
    void testCreateBookingSeatUnavailableThrows() {
        BookingRequest request = new BookingRequest(10L, 20L, List.of(101L));

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(ticketTypeRepository.findById(20L)).thenReturn(Optional.of(ticketType));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(seatRepository.findByIdAndIsBookedFalse(101L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> bookingService.createBooking(request, 1L));
    }

    @Test
    void testCancelBookingSuccess() {
        seat1.setBooked(true);
        Booking booking = Booking.builder()
                .id(500L)
                .user(user)
                .event(event)
                .ticketType(ticketType)
                .totalAmount(BigDecimal.valueOf(50))
                .status(BookingStatus.CONFIRMED)
                .build();

        BookingSeat bs = BookingSeat.builder().id(1L).booking(booking).seat(seat1).build();
        booking.setBookingSeats(List.of(bs));

        when(bookingRepository.findById(500L)).thenReturn(Optional.of(booking));

        bookingService.cancelBooking(500L, 1L);

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertFalse(seat1.isBooked());
        assertEquals(11, ticketType.getAvailableQuantity());
        verify(seatRepository).save(seat1);
        verify(ticketTypeRepository).save(ticketType);
        verify(bookingRepository).save(booking);
    }

    @Test
    void testCancelBookingNotOwnerThrows() {
        Booking booking = Booking.builder().id(500L).user(user).build();
        when(bookingRepository.findById(500L)).thenReturn(Optional.of(booking));

        // Different user ID 999L
        assertThrows(BadRequestException.class, () -> bookingService.cancelBooking(500L, 999L));
    }
}
