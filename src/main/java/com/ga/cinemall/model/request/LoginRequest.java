package com.ga.cinemall.model.request;

import lombok.Getter;
import lombok.Setter;

/** JSON body: {email}, {password}. **/
@Getter
@Setter
public class LoginRequest {
	private String email;
	private String password;
}
