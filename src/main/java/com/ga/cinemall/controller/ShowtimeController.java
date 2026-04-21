package com.ga.cinemall.controller;

import com.ga.cinemall.model.Showtime;
import com.ga.cinemall.service.ShowSeatService;
import com.ga.cinemall.service.ShowtimeService;
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
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

	private final ShowtimeService showtimeService;
	private final ShowSeatService showSeatService;

	@GetMapping
	public List<Showtime> getAllShowtimes() {
		return showtimeService.getAllShowtimes();
	}

	@GetMapping("/{showtimeId}")
	public Showtime getShowtimeById(@PathVariable Long showtimeId) {
		return showtimeService.getShowtimeById(showtimeId);
	}

	@GetMapping("/{showtimeId}/seats")
	public List<ShowSeatService.ShowSeatDto> getSeatMap(@PathVariable Long showtimeId) {
		return showSeatService.getSeatMapForShowtime(showtimeId);
	}

	@PostMapping
	@PreAuthorize("hasAuthority('ADMIN')")
	public Showtime createShowtime(@RequestBody Showtime showtimeObject) {
		return showtimeService.createShowtime(showtimeObject);
	}

	@PutMapping("/{showtimeId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public Showtime updateShowtime(@PathVariable Long showtimeId, @RequestBody Showtime showtimeObject) {
		return showtimeService.updateShowtime(showtimeId, showtimeObject);
	}

	@DeleteMapping("/{showtimeId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public void deleteShowtime(@PathVariable Long showtimeId) {
		showtimeService.deleteShowtime(showtimeId);
	}
}

