package com.app.eventhive.controller;

import com.app.eventhive.dto.BookingRequest;
import com.app.eventhive.dto.BookingResponse;
import com.app.eventhive.entity.BookingStatus;
import com.app.eventhive.security.SecurityUtils;
import com.app.eventhive.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private SecurityUtils securityUtils;

    private BookingController bookingController;

    private BookingResponse sampleResponse;

    @BeforeEach
    void setUp() {
        bookingController = new BookingController(bookingService, securityUtils);
        sampleResponse = new BookingResponse(10L, "Concert", "Venue", "VIP",
                List.of("A1"), BigDecimal.valueOf(100), BookingStatus.CONFIRMED, LocalDateTime.now());
    }

    @Test
    void testCreateBooking() {
        BookingRequest request = new BookingRequest(1L, 2L, List.of(5L));
        when(securityUtils.getCurrentUserId()).thenReturn(20L);
        when(bookingService.createBooking(request, 20L)).thenReturn(sampleResponse);

        ResponseEntity<BookingResponse> response = bookingController.createBooking(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().id());
    }

    @Test
    void testGetUserBookings() {
        when(securityUtils.getCurrentUserId()).thenReturn(20L);
        when(bookingService.getUserBookings(20L)).thenReturn(List.of(sampleResponse));

        ResponseEntity<List<BookingResponse>> response = bookingController.getUserBookings();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testCancelBooking() {
        when(securityUtils.getCurrentUserId()).thenReturn(20L);

        ResponseEntity<Void> response = bookingController.cancelBooking(10L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookingService).cancelBooking(10L, 20L);
    }
}
