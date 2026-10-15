package com.app.eventhive.repository;

import com.app.eventhive.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByEventId(Long eventId);

    List<Seat> findByEventIdAndIsBooked(Long eventId, boolean isBooked);

    List<Seat> findByEventIdAndTicketTypeId(Long eventId, Long ticketTypeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Seat> findByIdAndIsBookedFalse(Long seatId);
}
