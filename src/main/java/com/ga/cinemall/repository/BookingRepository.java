package com.ga.cinemall.repository;

import com.ga.cinemall.model.Booking;
import com.ga.cinemall.model.BookingStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	Optional<Booking> findByIdAndUser_Id(Long bookingId, Long userId);

	long countByShowtime_IdAndUser_IdAndStatus(Long showtimeId, Long userId, BookingStatus status);

	Optional<Booking> findFirstByShowtime_IdAndUser_IdAndStatusOrderByCreatedAtDesc(Long showtimeId, Long userId, BookingStatus status);

	List<Booking> findByUser_IdOrderByCreatedAtDesc(Long userId);
}

