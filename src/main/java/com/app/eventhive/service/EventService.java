package com.app.eventhive.service;

import com.app.eventhive.dto.*;
import com.app.eventhive.entity.*;
import com.app.eventhive.exception.BadRequestException;
import com.app.eventhive.exception.ResourceNotFoundException;
import com.app.eventhive.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final SeatRepository seatRepository;

    public EventService(EventRepository eventRepository,
                        CategoryRepository categoryRepository,
                        UserRepository userRepository,
                        TicketTypeRepository ticketTypeRepository,
                        SeatRepository seatRepository) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.ticketTypeRepository = ticketTypeRepository;
        this.seatRepository = seatRepository;
    }

    public EventResponse createEvent(EventRequest request, Long organizerId) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found"));

        Event event = Event.builder()
                .title(request.title())
                .description(request.description())
                .location(request.location())
                .startDateTime(request.startDateTime())
                .endDateTime(request.endDateTime())
                .imageUrl(request.imageUrl())
                .status(EventStatus.UPCOMING)
                .category(category)
                .organizer(organizer)
                .build();

        return toEventResponse(eventRepository.save(event));
    }

    public Page<EventResponse> getEvents(String search, Long categoryId, EventStatus status, int page, int size) {
        return eventRepository.searchEvents(search, categoryId, status, PageRequest.of(page, size))
                .map(this::toEventResponse);
    }

    public EventResponse getEventById(Long id) {
        return toEventResponse(eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id)));
    }

    public List<EventResponse> getOrganizerEvents(Long organizerId) {
        return eventRepository.findByOrganizerId(organizerId).stream()
                .map(this::toEventResponse)
                .toList();
    }

    @Transactional
    public EventResponse updateEvent(Long eventId, EventRequest request, Long organizerId) {
        Event event = getOwnedEvent(eventId, organizerId);

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setLocation(request.location());
        event.setStartDateTime(request.startDateTime());
        event.setEndDateTime(request.endDateTime());
        event.setImageUrl(request.imageUrl());
        event.setCategory(category);

        return toEventResponse(eventRepository.save(event));
    }

    public void deleteEvent(Long eventId, Long organizerId) {
        Event event = getOwnedEvent(eventId, organizerId);
        eventRepository.delete(event);
    }

    @Transactional
    public List<TicketType> addTicketTypes(Long eventId, List<TicketTypeRequest> requests, Long organizerId) {
        Event event = getOwnedEvent(eventId, organizerId);

        List<TicketType> ticketTypes = requests.stream().map(r -> TicketType.builder()
                .name(r.name())
                .price(r.price())
                .totalQuantity(r.totalQuantity())
                .availableQuantity(r.totalQuantity())
                .event(event)
                .build()).toList();

        return ticketTypeRepository.saveAll(ticketTypes);
    }

    @Transactional
    public List<Seat> addSeats(Long eventId, List<SeatRequest> requests, Long organizerId) {
        Event event = getOwnedEvent(eventId, organizerId);

        List<Seat> seats = requests.stream().map(r -> {
            TicketType ticketType = ticketTypeRepository.findById(r.ticketTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("TicketType not found: " + r.ticketTypeId()));
            return Seat.builder()
                    .seatNumber(r.seatNumber())
                    .isBooked(false)
                    .event(event)
                    .ticketType(ticketType)
                    .build();
        }).toList();

        return seatRepository.saveAll(seats);
    }

    public List<Seat> getEventSeats(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId);
        }
        return seatRepository.findByEventId(eventId);
    }

    public List<Seat> getAvailableSeats(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId);
        }
        return seatRepository.findByEventIdAndIsBooked(eventId, false);
    }

    // ---- Helpers ----

    private Event getOwnedEvent(Long eventId, Long organizerId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        if (!event.getOrganizer().getId().equals(organizerId)) {
            throw new BadRequestException("You are not the organizer of this event");
        }
        return event;
    }

    public EventResponse toEventResponse(Event event) {
        CategoryResponse categoryResponse = new CategoryResponse(
                event.getCategory().getId(), event.getCategory().getName());
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getStartDateTime(),
                event.getEndDateTime(),
                event.getImageUrl(),
                event.getStatus(),
                categoryResponse,
                event.getOrganizer().getName(),
                event.getCreatedAt()
        );
    }
}
