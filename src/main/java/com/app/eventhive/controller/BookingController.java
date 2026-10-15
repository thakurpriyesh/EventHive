package com.app.eventhive.controller;

import com.app.eventhive.dto.BookingRequest;
import com.app.eventhive.dto.BookingResponse;
import com.app.eventhive.security.SecurityUtils;
import com.app.eventhive.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final SecurityUtils securityUtils;

    public BookingController(BookingService bookingService, SecurityUtils securityUtils) {
        this.bookingService = bookingService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(request, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getUserBookings() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(bookingService.getBookingById(id, userId));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        bookingService.cancelBooking(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/organizer")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<List<BookingResponse>> getOrganizerBookings() {
        Long organizerId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(bookingService.getOrganizerBookings(organizerId));
    }
}
