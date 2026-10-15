package com.app.eventhive.repository;

import com.app.eventhive.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByEventOrganizerId(Long organizerId);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b " +
           "WHERE b.event.organizer.id = :organizerId AND b.status = com.app.eventhive.entity.BookingStatus.CONFIRMED")
    BigDecimal getTotalRevenueByOrganizerId(@Param("organizerId") Long organizerId);

    @Query("SELECT COUNT(b) FROM Booking b " +
           "WHERE b.event.organizer.id = :organizerId AND b.status = com.app.eventhive.entity.BookingStatus.CONFIRMED")
    long countConfirmedBookingsByOrganizerId(@Param("organizerId") Long organizerId);
}
