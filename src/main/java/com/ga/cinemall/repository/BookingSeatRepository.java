package com.ga.cinemall.repository;

import com.ga.cinemall.model.BookingSeat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
	List<BookingSeat> findByBooking_IdOrderByIdAsc(Long bookingId);
}

