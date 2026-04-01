package com.ga.cinemall.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * {@code message} holds the JWT on success, or an error string on failure.
 */
@Getter
@AllArgsConstructor
public class LoginResponse {
	private String message;
}
