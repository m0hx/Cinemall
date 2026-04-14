package com.ga.cinemall.service;

import com.ga.cinemall.model.Hall;
import com.ga.cinemall.model.HallStatus;
import com.ga.cinemall.repository.HallRepository;
import com.ga.cinemall.repository.HallSeatRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class HallService {

	private final HallRepository hallRepository;
	private final HallSeatRepository hallSeatRepository;

	public List<Hall> getAllHalls() {
		return hallRepository.findAll();
	}

	public Hall getHallById(Long hallId) {
		return hallRepository
				.findById(hallId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hall not found with id: " + hallId));
	}

	public Hall createHall(Hall hallObject) {
		hallObject.setId(null);
		String name = normalizeName(hallObject.getName());
		hallRepository
				.findByNameIgnoreCase(name)
				.ifPresent(h -> {
					throw new ResponseStatusException(HttpStatus.CONFLICT, "Hall name already exists");
				});
		hallObject.setName(name);
		if (hallObject.getStatus() == null) {
			hallObject.setStatus(HallStatus.ACTIVE);
		}
		return hallRepository.save(hallObject);
	}

	public Hall updateHall(Long hallId, Hall hallObject) {
		Hall existing = getHallById(hallId);
		String name = normalizeName(hallObject.getName());
		hallRepository
				.findByNameIgnoreCase(name)
				.filter(h -> !h.getId().equals(hallId))
				.ifPresent(h -> {
					throw new ResponseStatusException(HttpStatus.CONFLICT, "Hall name already exists");
				});
		existing.setName(name);
		if (hallObject.getStatus() != null) {
			existing.setStatus(hallObject.getStatus());
		}
		return hallRepository.save(existing);
	}

	public void deleteHall(Long hallId) {
		getHallById(hallId);
		if (hallSeatRepository.countByHall_Id(hallId) > 0) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete hall that still has seats");
		}
		hallRepository.deleteById(hallId);
	}

	private static String normalizeName(String name) {
		if (name == null || name.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hall name is required");
		}
		return name.trim();
	}
}

