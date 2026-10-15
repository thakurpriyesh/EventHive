package com.app.eventhive.controller;

import com.app.eventhive.dto.EventRequest;
import com.app.eventhive.dto.EventResponse;
import com.app.eventhive.dto.TicketTypeRequest;
import com.app.eventhive.dto.SeatRequest;
import com.app.eventhive.entity.EventStatus;
import com.app.eventhive.entity.Seat;
import com.app.eventhive.entity.TicketType;
import com.app.eventhive.security.SecurityUtils;
import com.app.eventhive.service.EventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final SecurityUtils securityUtils;

    public EventController(EventService eventService, SecurityUtils securityUtils) {
        this.eventService = eventService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<Page<EventResponse>> getEvents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) EventStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(eventService.getEvents(search, categoryId, status, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<List<EventResponse>> getMyEvents() {
        Long organizerId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(eventService.getOrganizerEvents(organizerId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        Long organizerId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(request, organizerId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id,
                                                     @Valid @RequestBody EventRequest request) {
        Long organizerId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(eventService.updateEvent(id, request, organizerId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        Long organizerId = securityUtils.getCurrentUserId();
        eventService.deleteEvent(id, organizerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/ticket-types")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<List<TicketType>> addTicketTypes(@PathVariable Long id,
                                                           @Valid @RequestBody List<TicketTypeRequest> requests) {
        Long organizerId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.addTicketTypes(id, requests, organizerId));
    }

    @PostMapping("/{id}/seats")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<List<Seat>> addSeats(@PathVariable Long id,
                                               @Valid @RequestBody List<SeatRequest> requests) {
        Long organizerId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.addSeats(id, requests, organizerId));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<Seat>> getEventSeats(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventSeats(id));
    }

    @GetMapping("/{id}/seats/available")
    public ResponseEntity<List<Seat>> getAvailableSeats(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getAvailableSeats(id));
    }
}
