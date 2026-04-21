package com.ga.cinemall.controller;

import com.ga.cinemall.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

	private final BookingService bookingService;

	@PostMapping("/reserve")
	@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
	public BookingService.ReserveResponse reserve(@RequestBody BookingService.ReserveRequest req) {
		return bookingService.reserveSeats(req);
	}
}

