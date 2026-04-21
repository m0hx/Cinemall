package com.ga.cinemall.service;

import com.ga.cinemall.model.HallSeat;
import com.ga.cinemall.model.ShowSeat;
import com.ga.cinemall.model.ShowSeatStatus;
import com.ga.cinemall.model.Showtime;
import com.ga.cinemall.repository.HallSeatRepository;
import com.ga.cinemall.repository.ShowSeatRepository;
import com.ga.cinemall.repository.ShowtimeRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ShowSeatService {

	private final ShowtimeRepository showtimeRepository;
	private final HallSeatRepository hallSeatRepository;
	private final ShowSeatRepository showSeatRepository;

	public record ShowSeatDto(
			Long id,
			Long hallSeatId,
			String rowLabel,
			Integer seatNumber,
			String seatLabel,
			String type,
			boolean accessible,
			ShowSeatStatus status) {}

	@Transactional
	public List<ShowSeatDto> getSeatMapForShowtime(Long showtimeId) {
		Showtime showtime = showtimeRepository
				.findById(showtimeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Showtime not found with id: " + showtimeId));

		ensureInitialized(showtime);
		expireStaleReservations(showtimeId);

		List<ShowSeat> seats = showSeatRepository.findByShowtime_IdOrderByHallSeat_RowLabelAscHallSeat_SeatNumberAsc(showtimeId);
		List<ShowSeatDto> result = new ArrayList<>(seats.size());
		for (ShowSeat s : seats) {
			HallSeat hs = s.getHallSeat();
			result.add(new ShowSeatDto(
					s.getId(),
					hs.getId(),
					hs.getRowLabel(),
					hs.getSeatNumber(),
					hs.getSeatLabel(),
					hs.getType() != null ? hs.getType().name() : null,
					hs.isAccessible(),
					s.getStatus()));
		}
		return result;
	}

	/**
	 * Clears expired holds so the public seat map matches the 5-minute window after refresh.
	 */
	@Transactional
	public void expireStaleReservations(Long showtimeId) {
		Instant now = Instant.now();
		List<ShowSeat> seats =
				showSeatRepository.findByShowtime_IdOrderByHallSeat_RowLabelAscHallSeat_SeatNumberAsc(showtimeId);
		for (ShowSeat s : seats) {
			if (s.getStatus() != ShowSeatStatus.RESERVED) {
				continue;
			}
			Instant until = s.getReservedUntil();
			if (until != null && until.isAfter(now)) {
				continue;
			}
			s.setStatus(ShowSeatStatus.AVAILABLE);
			s.setReservedByUser(null);
			s.setReservedUntil(null);
			showSeatRepository.save(s);
		}
	}

	private void ensureInitialized(Showtime showtime) {
		Long showtimeId = showtime.getId();
		if (showSeatRepository.countByShowtime_Id(showtimeId) > 0) return;

		List<HallSeat> hallSeats = hallSeatRepository.findByHall_IdOrderByRowLabelAscSeatNumberAsc(showtime.getHall().getId());
		if (hallSeats.isEmpty()) return;

		for (HallSeat hs : hallSeats) {
			if (showSeatRepository.existsByShowtime_IdAndHallSeat_Id(showtimeId, hs.getId())) continue;
			ShowSeat s = new ShowSeat();
			s.setShowtime(showtime);
			s.setHallSeat(hs);
			s.setStatus(ShowSeatStatus.AVAILABLE);
			showSeatRepository.save(s);
		}
	}
}

