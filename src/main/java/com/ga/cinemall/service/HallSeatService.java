package com.ga.cinemall.service;

import com.ga.cinemall.model.Hall;
import com.ga.cinemall.model.HallSeat;
import com.ga.cinemall.model.SeatType;
import com.ga.cinemall.repository.HallRepository;
import com.ga.cinemall.repository.HallSeatRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class HallSeatService {

	private final HallRepository hallRepository;
	private final HallSeatRepository hallSeatRepository;

	public List<HallSeat> getSeatsForHall(Long hallId) {
		requireHall(hallId);
		return hallSeatRepository.findByHall_IdOrderByRowLabelAscSeatNumberAsc(hallId);
	}

	public HallSeat createHallSeat(Long hallId, HallSeat seatObject) {
		Hall hall = requireHall(hallId);
		seatObject.setId(null);
		seatObject.setHall(hall);
		seatObject.setRowLabel(normalizeRowLabel(seatObject.getRowLabel()));
		seatObject.setSeatNumber(normalizeSeatNumber(seatObject.getSeatNumber()));
		seatObject.setSeatLabel(buildSeatLabel(seatObject.getRowLabel(), seatObject.getSeatNumber()));
		if (hallSeatRepository.existsByHall_IdAndSeatLabel(hallId, seatObject.getSeatLabel())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Seat already exists in this hall: " + seatObject.getSeatLabel());
		}
		seatObject.setType(normalizeType(seatObject.getType()));
		return hallSeatRepository.save(seatObject);
	}

	public HallSeat updateHallSeat(Long hallSeatId, HallSeat seatObject) {
		HallSeat existing = hallSeatRepository
				.findById(hallSeatId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hall seat not found with id: " + hallSeatId));

		// Keep row/seatNumber/seatLabel immutable for safety (uniqueness + references).
		if (seatObject.getType() != null) {
			existing.setType(seatObject.getType());
		}
		existing.setAccessible(seatObject.isAccessible());

		return hallSeatRepository.save(existing);
	}

	public void deleteHallSeat(Long hallSeatId) {
		HallSeat seat = hallSeatRepository
				.findById(hallSeatId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hall seat not found with id: " + hallSeatId));
		hallSeatRepository.delete(seat);
	}

	private Hall requireHall(Long hallId) {
		return hallRepository
				.findById(hallId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hall not found with id: " + hallId));
	}

	private static String normalizeRowLabel(String rowLabel) {
		if (rowLabel == null || rowLabel.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Row label is required");
		}
		return rowLabel.trim().toUpperCase();
	}

	private static int normalizeSeatNumber(Integer seatNumber) {
		if (seatNumber == null || seatNumber < 1) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seat number must be >= 1");
		}
		return seatNumber;
	}

	private static String buildSeatLabel(String rowLabel, int seatNumber) {
		return rowLabel + seatNumber;
	}

	private static SeatType normalizeType(SeatType type) {
		return type != null ? type : SeatType.STANDARD;
	}
}

