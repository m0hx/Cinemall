package com.ga.cinemall.controller;

import com.ga.cinemall.service.AdminUserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

	private final AdminUserService adminUserService;

	@GetMapping
	@PreAuthorize("hasAuthority('ADMIN')")
	public List<AdminUserService.UserAdminDto> list() {
		return adminUserService.listUsers();
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public AdminUserService.UserAdminDto get(@PathVariable Long id) {
		return adminUserService.getUser(id);
	}

	@PatchMapping("/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public AdminUserService.UserAdminDto update(@PathVariable Long id, @RequestBody AdminUserService.UpdateUserRequest req) {
		return adminUserService.updateUser(id, req);
	}
}

