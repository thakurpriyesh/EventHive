package com.app.eventhive.controller;

import com.app.eventhive.dto.CategoryResponse;
import com.app.eventhive.dto.EventRequest;
import com.app.eventhive.dto.EventResponse;
import com.app.eventhive.entity.EventStatus;
import com.app.eventhive.entity.Seat;
import com.app.eventhive.security.SecurityUtils;
import com.app.eventhive.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventService eventService;

    @Mock
    private SecurityUtils securityUtils;

    private EventController eventController;

    private EventResponse sampleResponse;

    @BeforeEach
    void setUp() {
        eventController = new EventController(eventService, securityUtils);
        sampleResponse = new EventResponse(1L, "Festival", "Music fest", "Central Park",
                LocalDateTime.now(), LocalDateTime.now().plusHours(4), null,
                EventStatus.UPCOMING, new CategoryResponse(1L, "Music"), "Org", LocalDateTime.now());
    }

    @Test
    void testGetEvents() {
        Page<EventResponse> page = new PageImpl<>(List.of(sampleResponse));
        when(eventService.getEvents("fest", 1L, EventStatus.UPCOMING, 0, 10)).thenReturn(page);

        ResponseEntity<Page<EventResponse>> response = eventController.getEvents("fest", 1L, EventStatus.UPCOMING, 0, 10);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void testGetEventById() {
        when(eventService.getEventById(1L)).thenReturn(sampleResponse);

        ResponseEntity<EventResponse> response = eventController.getEventById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
    }

    @Test
    void testCreateEvent() {
        EventRequest request = new EventRequest("Festival", "Music fest", "Central Park",
                LocalDateTime.now(), LocalDateTime.now().plusHours(4), 1L, null);
        when(securityUtils.getCurrentUserId()).thenReturn(10L);
        when(eventService.createEvent(request, 10L)).thenReturn(sampleResponse);

        ResponseEntity<EventResponse> response = eventController.createEvent(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Festival", response.getBody().title());
    }

    @Test
    void testDeleteEvent() {
        when(securityUtils.getCurrentUserId()).thenReturn(10L);
        ResponseEntity<Void> response = eventController.deleteEvent(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(eventService).deleteEvent(1L, 10L);
    }

    @Test
    void testGetEventSeats() {
        Seat seat = Seat.builder().id(100L).seatNumber("B1").isBooked(false).build();
        when(eventService.getEventSeats(1L)).thenReturn(List.of(seat));

        ResponseEntity<List<Seat>> response = eventController.getEventSeats(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("B1", response.getBody().get(0).getSeatNumber());
    }
}
