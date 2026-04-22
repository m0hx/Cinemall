package com.ga.cinemall.service;

import com.ga.cinemall.model.Booking;
import com.ga.cinemall.model.BookingSeat;
import com.ga.cinemall.model.BookingStatus;
import com.ga.cinemall.repository.BookingRepository;
import com.ga.cinemall.repository.BookingSeatRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AdminBookingService {

	private final BookingRepository bookingRepository;
	private final BookingSeatRepository bookingSeatRepository;

	public record BookingAdminDto(
			Long id,
			BookingStatus status,
			Instant createdAt,
			Instant confirmedAt,
			Long userId,
			String userEmail,
			Long showtimeId,
			Instant startsAt,
			Instant endsAt,
			String movieTitle,
			List<String> seatLabels) {}

	@Transactional(readOnly = true)
	public List<BookingAdminDto> listAll() {
		return bookingRepository.findAllByOrderByCreatedAtDesc().stream()
				.map(this::toDto)
				.toList();
	}

	@Transactional(readOnly = true)
	public BookingAdminDto getOne(Long id) {
		Booking b = bookingRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
		return toDto(b);
	}

	private BookingAdminDto toDto(Booking b) {
		List<BookingSeat> seats = bookingSeatRepository.findByBooking_IdOrderByIdAsc(b.getId());
		List<String> labels = seats.stream()
				.map((bs) -> bs.getShowSeat().getHallSeat().getSeatLabel())
				.toList();

		return new BookingAdminDto(
				b.getId(),
				b.getStatus(),
				b.getCreatedAt(),
				b.getConfirmedAt(),
				b.getUser() != null ? b.getUser().getId() : null,
				b.getUser() != null ? b.getUser().getEmail() : null,
				b.getShowtime() != null ? b.getShowtime().getId() : null,
				b.getShowtime() != null ? b.getShowtime().getStartsAt() : null,
				b.getShowtime() != null ? b.getShowtime().getEndsAt() : null,
				b.getShowtime() != null && b.getShowtime().getMovie() != null ? b.getShowtime().getMovie().getTitle() : null,
				labels);
	}
}

