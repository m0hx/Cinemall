package com.ga.cinemall.model.request;

import lombok.Getter;
import lombok.Setter;

/** JSON body: {@code email}, {@code password}. */
@Getter
@Setter
public class LoginRequest {
	private String email;
	private String password;
}
