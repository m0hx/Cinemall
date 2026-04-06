package com.ga.cinemall.controller;

import com.ga.cinemall.model.User;
import com.ga.cinemall.model.request.LoginRequest;
import com.ga.cinemall.model.request.RegisterRequest;
import com.ga.cinemall.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Auth endpoints {/auth/users}. */
@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/register")
	public User register(@RequestBody RegisterRequest request) {
		return userService.createUser(request);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		return userService.loginUser(loginRequest);
	}
}
