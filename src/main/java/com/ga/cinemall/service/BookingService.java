package com.ga.cinemall.service;

import com.ga.cinemall.model.User;
import com.ga.cinemall.repository.ShowtimeRepository;
import com.ga.cinemall.security.MyUserDetails;
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

	public record ReserveRequest(Long showtimeId, List<Long> showSeatIds) {}

	public record ReserveResponse(java.time.Instant reservedUntil, List<ShowSeatService.ShowSeatDto> seats) {}

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
