package com.ga.cinemall.controller;

import com.ga.cinemall.model.Hall;
import com.ga.cinemall.model.HallSeat;
import com.ga.cinemall.service.HallSeatService;
import com.ga.cinemall.service.HallService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/halls")
@RequiredArgsConstructor
public class HallController {

	private final HallService hallService;
	private final HallSeatService hallSeatService;

	@GetMapping
	public List<Hall> getAllHalls() {
		return hallService.getAllHalls();
	}

	@GetMapping("/{hallId}")
	public Hall getHallById(@PathVariable Long hallId) {
		return hallService.getHallById(hallId);
	}

	@GetMapping("/{hallId}/seats")
	public List<HallSeat> getSeatsForHall(@PathVariable Long hallId) {
		return hallSeatService.getSeatsForHall(hallId);
	}

	@PostMapping
	@PreAuthorize("hasAuthority('ADMIN')")
	public Hall createHall(@RequestBody Hall hallObject) {
		return hallService.createHall(hallObject);
	}

	@PutMapping("/{hallId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public Hall updateHall(@PathVariable Long hallId, @RequestBody Hall hallObject) {
		return hallService.updateHall(hallId, hallObject);
	}

	@DeleteMapping("/{hallId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public void deleteHall(@PathVariable Long hallId) {
		hallService.deleteHall(hallId);
	}

	@PostMapping("/{hallId}/seats")
	@PreAuthorize("hasAuthority('ADMIN')")
	public HallSeat createSeat(@PathVariable Long hallId, @RequestBody HallSeat seatObject) {
		return hallSeatService.createHallSeat(hallId, seatObject);
	}

	@PutMapping("/seats/{hallSeatId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public HallSeat updateSeat(@PathVariable Long hallSeatId, @RequestBody HallSeat seatObject) {
		return hallSeatService.updateHallSeat(hallSeatId, seatObject);
	}

	@DeleteMapping("/seats/{hallSeatId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public void deleteSeat(@PathVariable Long hallSeatId) {
		hallSeatService.deleteHallSeat(hallSeatId);
	}
}

