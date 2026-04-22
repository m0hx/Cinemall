package com.ga.cinemall.controller;

import com.ga.cinemall.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	@GetMapping("/me")
	@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
	public AccountService.MeResponse me() {
		return accountService.getMe();
	}

	@PatchMapping("/me")
	@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
	public AccountService.MeResponse updateMe(@RequestBody AccountService.UpdateMeRequest req) {
		return accountService.updateMe(req);
	}
}

