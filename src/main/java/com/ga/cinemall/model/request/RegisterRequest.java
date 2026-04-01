package com.ga.cinemall.model.request;

import lombok.Getter;
import lombok.Setter;

/** Signup body — maps to {@link com.ga.cinemall.model.User} in {@code UserService}. */
@Getter
@Setter
public class RegisterRequest {
	private String email;
	private String password;
	private String name;
}
