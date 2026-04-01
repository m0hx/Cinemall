package com.ga.cinemall.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@GetMapping("/")
	public String home() {
		return "Cinemall API is up. React frontend will be a separate app.";
	}

	@GetMapping("/login")
	public String loginInfo() {
		return "Login UI: React app. API: POST /auth/users/login";
	}
}
