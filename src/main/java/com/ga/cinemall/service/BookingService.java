package com.ga.cinemall.service;

import com.ga.cinemall.model.ShowSeat;
import com.ga.cinemall.model.ShowSeatStatus;
import com.ga.cinemall.model.User;
import com.ga.cinemall.repository.ShowSeatRepository;
import com.ga.cinemall.repository.ShowtimeRepository;
import com.ga.cinemall.security.MyUserDetails;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BookingService {

	private static final int MAX_SEATS_PER_RESERVE = 8;

	private final ShowtimeRepository showtimeRepository;
	private final ShowSeatRepository showSeatRepository;
	private final ShowSeatService showSeatService;

	public record ReserveRequest(Long showtimeId, List<Long> showSeatIds) {}

	public record ReserveResponse(Instant reservedUntil, List<ShowSeatService.ShowSeatDto> seats) {}

	@Transactional
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
		if (req.showSeatIds().size() > MAX_SEATS_PER_RESERVE) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too many seats requested (max " + MAX_SEATS_PER_RESERVE + ")");
		}

		// Ensure showtime exists (and gives a clean 404).
		showtimeRepository
				.findById(req.showtimeId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Showtime not found with id: " + req.showtimeId()));

		showSeatService.expireStaleReservations(req.showtimeId());

		User currentUser = requireCurrentUser();

		Instant now = Instant.now();
		Instant reservedUntil = now.plus(5, ChronoUnit.MINUTES);

		long activeHolds = showSeatRepository.countByShowtime_IdAndReservedByUser_IdAndStatusAndReservedUntilAfter(
				req.showtimeId(),
				currentUser.getId(),
				ShowSeatStatus.RESERVED,
				now);
		if (activeHolds > 0) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"You already have reserved seats for this showtime. Confirm/cancel or wait for expiry.");
		}

		List<ShowSeat> seats = showSeatRepository.findByShowtime_IdAndIdIn(req.showtimeId(), req.showSeatIds());
		if (seats.size() != req.showSeatIds().size()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more showSeatIds are invalid for this showtime");
		}

		// Validate and reserve.
		for (ShowSeat s : seats) {
			if (s.getStatus() == ShowSeatStatus.BOOKED) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Seat already booked: " + s.getHallSeat().getSeatLabel());
			}
			if (s.getStatus() == ShowSeatStatus.RESERVED) {
				Instant until = s.getReservedUntil();
				if (until != null && until.isAfter(now)) {
					throw new ResponseStatusException(HttpStatus.CONFLICT, "Seat already reserved: " + s.getHallSeat().getSeatLabel());
				}
				// If expired, treat as available again.
				s.setStatus(ShowSeatStatus.AVAILABLE);
				s.setReservedByUser(null);
				s.setReservedUntil(null);
			}
		}

		for (ShowSeat s : seats) {
			s.setStatus(ShowSeatStatus.RESERVED);
			s.setReservedByUser(currentUser);
			s.setReservedUntil(reservedUntil);
			showSeatRepository.save(s);
		}

		// Return the full seat map (handy for UI refresh).
		List<ShowSeat> all = showSeatRepository.findByShowtime_IdOrderByHallSeat_RowLabelAscHallSeat_SeatNumberAsc(req.showtimeId());
		List<ShowSeatService.ShowSeatDto> dtos = new ArrayList<>(all.size());
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

		return new ReserveResponse(reservedUntil, dtos);
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

