package com.ga.cinemall.controller;

import com.ga.cinemall.service.AdminBookingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {

	private final AdminBookingService adminBookingService;

	@GetMapping
	@PreAuthorize("hasAuthority('ADMIN')")
	public List<AdminBookingService.BookingAdminDto> list() {
		return adminBookingService.listAll();
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public AdminBookingService.BookingAdminDto get(@PathVariable Long id) {
		return adminBookingService.getOne(id);
	}
}

