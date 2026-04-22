package com.ga.cinemall.controller;

import com.ga.cinemall.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

	@PostMapping("/{bookingId}/confirm")
	@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
	public BookingService.ConfirmResponse confirm(
			@PathVariable Long bookingId,
			@RequestBody BookingService.ConfirmRequest req) {
		return bookingService.confirmBooking(bookingId, req);
	}

	@GetMapping("/pending")
	@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
	public BookingService.PendingResponse pending(@RequestParam Long showtimeId) {
		return bookingService.getPendingForShowtime(showtimeId);
	}

	@PostMapping("/{bookingId}/cancel")
	@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
	public BookingService.CancelResponse cancel(@PathVariable Long bookingId) {
		return bookingService.cancelBooking(bookingId);
	}

	@GetMapping
	@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
	public java.util.List<BookingService.BookingSummaryResponse> listMine() {
		return bookingService.listMyBookings();
	}

	@GetMapping("/{bookingId}")
	@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
	public BookingService.BookingDetailResponse getMine(@PathVariable Long bookingId) {
		return bookingService.getMyBooking(bookingId);
	}
}

