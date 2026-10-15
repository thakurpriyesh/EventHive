package com.app.eventhive.service;

import com.app.eventhive.dto.EventRequest;
import com.app.eventhive.dto.EventResponse;
import com.app.eventhive.dto.SeatRequest;
import com.app.eventhive.dto.TicketTypeRequest;
import com.app.eventhive.entity.*;
import com.app.eventhive.exception.BadRequestException;
import com.app.eventhive.exception.ResourceNotFoundException;
import com.app.eventhive.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TicketTypeRepository ticketTypeRepository;
    @Mock
    private SeatRepository seatRepository;

    private EventService eventService;

    private User organizer;
    private Category category;
    private Event event;

    @BeforeEach
    void setUp() {
        eventService = new EventService(eventRepository, categoryRepository, userRepository, ticketTypeRepository, seatRepository);

        organizer = User.builder().id(10L).name("Org Name").email("org@example.com").role(Role.ORGANIZER).build();
        category = Category.builder().id(1L).name("Music").build();
        event = Event.builder()
                .id(100L)
                .title("Rock Concert")
                .description("Live Rock")
                .location("Arena 1")
                .startDateTime(LocalDateTime.now().plusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(1).plusHours(3))
                .status(EventStatus.UPCOMING)
                .category(category)
                .organizer(organizer)
                .build();
    }

    @Test
    void testCreateEventSuccess() {
        EventRequest request = new EventRequest("Rock Concert", "Live Rock", "Arena 1",
                event.getStartDateTime(), event.getEndDateTime(), 1L, null);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(userRepository.findById(10L)).thenReturn(Optional.of(organizer));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventResponse response = eventService.createEvent(request, 10L);
        assertNotNull(response);
        assertEquals("Rock Concert", response.title());
        assertEquals("Org Name", response.organizerName());
    }

    @Test
    void testCreateEventCategoryNotFoundThrows() {
        EventRequest request = new EventRequest("Rock Concert", "Live Rock", "Arena 1",
                LocalDateTime.now(), LocalDateTime.now(), 999L, null);
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.createEvent(request, 10L));
    }

    @Test
    void testGetEvents() {
        Page<Event> page = new PageImpl<>(List.of(event));
        when(eventRepository.searchEvents("Rock", 1L, EventStatus.UPCOMING, PageRequest.of(0, 10))).thenReturn(page);

        Page<EventResponse> result = eventService.getEvents("Rock", 1L, EventStatus.UPCOMING, 0, 10);
        assertEquals(1, result.getTotalElements());
        assertEquals("Rock Concert", result.getContent().get(0).title());
    }

    @Test
    void testGetEventByIdSuccess() {
        when(eventRepository.findById(100L)).thenReturn(Optional.of(event));
        EventResponse response = eventService.getEventById(100L);
        assertEquals(100L, response.id());
    }

    @Test
    void testGetEventByIdNotFound() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> eventService.getEventById(999L));
    }

    @Test
    void testUpdateEventNotOwnerThrowsBadRequest() {
        EventRequest request = new EventRequest("Updated Title", "Desc", "Loc",
                LocalDateTime.now(), LocalDateTime.now(), 1L, null);

        when(eventRepository.findById(100L)).thenReturn(Optional.of(event));

        // Wrong organizer ID 99L instead of 10L
        assertThrows(BadRequestException.class, () -> eventService.updateEvent(100L, request, 99L));
    }

    @Test
    void testDeleteEventSuccess() {
        when(eventRepository.findById(100L)).thenReturn(Optional.of(event));
        eventService.deleteEvent(100L, 10L);
        verify(eventRepository).delete(event);
    }

    @Test
    void testAddTicketTypes() {
        TicketTypeRequest ttr = new TicketTypeRequest("VIP", BigDecimal.valueOf(150), 50);
        when(eventRepository.findById(100L)).thenReturn(Optional.of(event));
        when(ticketTypeRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<TicketType> result = eventService.addTicketTypes(100L, List.of(ttr), 10L);
        assertEquals(1, result.size());
        assertEquals("VIP", result.get(0).getName());
        assertEquals(50, result.get(0).getAvailableQuantity());
    }

    @Test
    void testAddSeats() {
        TicketType ticketType = TicketType.builder().id(5L).name("VIP").build();
        SeatRequest sr = new SeatRequest("A1", 5L);

        when(eventRepository.findById(100L)).thenReturn(Optional.of(event));
        when(ticketTypeRepository.findById(5L)).thenReturn(Optional.of(ticketType));
        when(seatRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Seat> seats = eventService.addSeats(100L, List.of(sr), 10L);
        assertEquals(1, seats.size());
        assertEquals("A1", seats.get(0).getSeatNumber());
        assertFalse(seats.get(0).isBooked());
    }

    @Test
    void testGetEventSeatsNotFound() {
        when(eventRepository.existsById(999L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> eventService.getEventSeats(999L));
    }
}
