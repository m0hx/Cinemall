package com.ga.cinemall.service;

import com.ga.cinemall.model.Booking;
import com.ga.cinemall.model.BookingSeat;
import com.ga.cinemall.model.BookingStatus;
import com.ga.cinemall.model.ShowSeat;
import com.ga.cinemall.model.ShowSeatStatus;
import com.ga.cinemall.model.User;
import com.ga.cinemall.repository.BookingRepository;
import com.ga.cinemall.repository.BookingSeatRepository;
import com.ga.cinemall.repository.ShowSeatRepository;
import com.ga.cinemall.repository.ShowtimeRepository;
import com.ga.cinemall.security.MyUserDetails;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BookingService {

	private final ReentrantLock reserveLock = new ReentrantLock();

	private final ShowtimeRepository showtimeRepository;
	private final BookingSeatHoldService seatHoldService;
	private final BookingRepository bookingRepository;
	private final BookingSeatRepository bookingSeatRepository;
	private final ShowSeatRepository showSeatRepository;

	public record ReserveRequest(Long showtimeId, List<Long> showSeatIds) {}

	public record ReserveResponse(Long bookingId, java.time.Instant reservedUntil, List<ShowSeatService.ShowSeatDto> seats) {}

	public record ConfirmRequest(String outcome) {}

	public record ConfirmResponse(Long bookingId, BookingStatus status, List<ShowSeatService.ShowSeatDto> seats) {}

	public record PendingResponse(Long bookingId, java.time.Instant reservedUntil) {}

	public record CancelResponse(Long bookingId, BookingStatus status, List<ShowSeatService.ShowSeatDto> seats) {}

	public ReserveResponse reserveSeats(ReserveRequest req) {
		if (req == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body is required");
		}
		if (req.showtimeId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "showtimeId is required");
		}
		if (req.showSeatIds() == null || req.showSeatIds().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "showSeatIds must not be empty");
		}

		showtimeRepository
				.findById(req.showtimeId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Showtime not found with id: " + req.showtimeId()));

		User currentUser = requireCurrentUser();

		reserveLock.lock();
		try {
			return seatHoldService.applyHold(req, currentUser);
		} finally {
			reserveLock.unlock();
		}
	}

	public PendingResponse getPendingForShowtime(Long showtimeId) {
		if (showtimeId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "showtimeId is required");
		}
		User currentUser = requireCurrentUser();

		Booking booking = bookingRepository
				.findFirstByShowtime_IdAndUser_IdAndStatusOrderByCreatedAtDesc(showtimeId, currentUser.getId(), BookingStatus.PENDING)
				.orElse(null);
		if (booking == null) return new PendingResponse(null, null);

		Instant reservedUntil = null;
		List<BookingSeat> seats = bookingSeatRepository.findByBooking_IdOrderByIdAsc(booking.getId());
		for (BookingSeat bs : seats) {
			Instant until = bs.getShowSeat().getReservedUntil();
			if (until != null && (reservedUntil == null || until.isAfter(reservedUntil))) {
				reservedUntil = until;
			}
		}
		return new PendingResponse(booking.getId(), reservedUntil);
	}

	public CancelResponse cancelBooking(Long bookingId) {
		if (bookingId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookingId is required");
		}
		User currentUser = requireCurrentUser();

		reserveLock.lock();
		try {
			Booking booking = bookingRepository
					.findByIdAndUser_Id(bookingId, currentUser.getId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

			if (booking.getStatus() != BookingStatus.PENDING) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking is not pending");
			}

			List<BookingSeat> bookingSeats = bookingSeatRepository.findByBooking_IdOrderByIdAsc(booking.getId());
			for (BookingSeat bs : bookingSeats) {
				ShowSeat s = bs.getShowSeat();
				if (s.getStatus() == ShowSeatStatus.RESERVED
						&& s.getReservedByUser() != null
						&& s.getReservedByUser().getId().equals(currentUser.getId())) {
					s.setStatus(ShowSeatStatus.AVAILABLE);
					s.setReservedByUser(null);
					s.setReservedUntil(null);
					showSeatRepository.save(s);
				}
			}

			// Important: remove join rows so the same seat can be used in future bookings.
			bookingSeatRepository.deleteAll(bookingSeats);

			booking.setStatus(BookingStatus.CANCELLED);
			bookingRepository.save(booking);

			List<ShowSeat> all =
					showSeatRepository.findByShowtime_IdOrderByHallSeat_RowLabelAscHallSeat_SeatNumberAsc(booking.getShowtime().getId());
			List<ShowSeatService.ShowSeatDto> dtos = new java.util.ArrayList<>(all.size());
			for (ShowSeat s : all) {
				var hs = s.getHallSeat();
				dtos.add(new ShowSeatService.ShowSeatDto(
						s.getId(),
						hs.getId(),
						hs.getRowLabel(),
						hs.getSeatNumber(),
						hs.getSeatLabel(),
						hs.getType() != null ? hs.getType().name() : null,
						hs.isAccessible(),
						s.getStatus()));
			}
			return new CancelResponse(booking.getId(), booking.getStatus(), dtos);
		} finally {
			reserveLock.unlock();
		}
	}

	public ConfirmResponse confirmBooking(Long bookingId, ConfirmRequest req) {
		if (bookingId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookingId is required");
		}
		if (req == null || req.outcome() == null || req.outcome().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "outcome is required");
		}

		User currentUser = requireCurrentUser();

		reserveLock.lock();
		try {
			Booking booking = bookingRepository
					.findByIdAndUser_Id(bookingId, currentUser.getId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

			if (booking.getStatus() != BookingStatus.PENDING) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking is not pending");
			}

			List<BookingSeat> bookingSeats = bookingSeatRepository.findByBooking_IdOrderByIdAsc(booking.getId());
			if (bookingSeats.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking has no seats");
			}

			Instant now = Instant.now();

			// Validate hold is still valid and owned by the same user.
			for (BookingSeat bs : bookingSeats) {
				ShowSeat s = bs.getShowSeat();
				if (s.getStatus() != ShowSeatStatus.RESERVED) {
					throw new ResponseStatusException(HttpStatus.CONFLICT, "Seat is no longer reserved: " + s.getHallSeat().getSeatLabel());
				}
				if (s.getReservedByUser() == null || !s.getReservedByUser().getId().equals(currentUser.getId())) {
					throw new ResponseStatusException(HttpStatus.CONFLICT, "Seat is reserved by another user: " + s.getHallSeat().getSeatLabel());
				}
				Instant until = s.getReservedUntil();
				if (until == null || !until.isAfter(now)) {
					throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservation expired for seat: " + s.getHallSeat().getSeatLabel());
				}
			}

			boolean success = "success".equalsIgnoreCase(req.outcome());
			if (success) {
				for (BookingSeat bs : bookingSeats) {
					ShowSeat s = bs.getShowSeat();
					s.setStatus(ShowSeatStatus.BOOKED);
					s.setReservedByUser(null);
					s.setReservedUntil(null);
					showSeatRepository.save(s);
				}
				booking.setStatus(BookingStatus.CONFIRMED);
				booking.setConfirmedAt(now);
				bookingRepository.save(booking);
			} else {
				// Payment failed: keep booking pending (seats stay reserved until expiry).
			}

			List<ShowSeat> all =
					showSeatRepository.findByShowtime_IdOrderByHallSeat_RowLabelAscHallSeat_SeatNumberAsc(booking.getShowtime().getId());
			List<ShowSeatService.ShowSeatDto> dtos = new java.util.ArrayList<>(all.size());
			for (ShowSeat s : all) {
				var hs = s.getHallSeat();
				dtos.add(new ShowSeatService.ShowSeatDto(
						s.getId(),
						hs.getId(),
						hs.getRowLabel(),
						hs.getSeatNumber(),
						hs.getSeatLabel(),
						hs.getType() != null ? hs.getType().name() : null,
						hs.isAccessible(),
						s.getStatus()));
			}
			return new ConfirmResponse(booking.getId(), booking.getStatus(), dtos);
		} finally {
			reserveLock.unlock();
		}
	}

	private static User requireCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getPrincipal() == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
		}
		Object principal = auth.getPrincipal();
		if (!(principal instanceof MyUserDetails)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
		}
		return ((MyUserDetails) principal).getUser();
	}
}
