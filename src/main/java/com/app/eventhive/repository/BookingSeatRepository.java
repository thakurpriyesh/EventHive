package com.app.eventhive.repository;

import com.app.eventhive.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
    boolean existsBySeatId(Long seatId);
}
