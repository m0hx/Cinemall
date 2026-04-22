package com.ga.cinemall.service;

import com.ga.cinemall.model.User;
import com.ga.cinemall.repository.UserRepository;
import com.ga.cinemall.security.MyUserDetails;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public record MeResponse(Long id, String email, String displayName, Instant emailVerifiedAt) {}

	public record UpdateMeRequest(String displayName, String currentPassword, String newPassword) {}

	@Transactional(readOnly = true)
	public MeResponse getMe() {
		User u = requireCurrentUser();
		return new MeResponse(u.getId(), u.getEmail(), u.getDisplayName(), u.getEmailVerifiedAt());
	}

	@Transactional
	public MeResponse updateMe(UpdateMeRequest req) {
		if (req == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body is required");

		User u = requireCurrentUser();

		if (req.displayName() != null) {
			String next = req.displayName().trim();
			if (next.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "displayName must not be empty");
			u.setDisplayName(next);
		}

		if (req.newPassword() != null && !req.newPassword().isBlank()) {
			if (req.currentPassword() == null || req.currentPassword().isBlank()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "currentPassword is required to change password");
			}
			if (!passwordEncoder.matches(req.currentPassword(), u.getPasswordHash())) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Current password is incorrect");
			}
			u.setPasswordHash(passwordEncoder.encode(req.newPassword()));
		}

		userRepository.save(u);
		return new MeResponse(u.getId(), u.getEmail(), u.getDisplayName(), u.getEmailVerifiedAt());
	}

	private static User requireCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getPrincipal() == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
		}
		Object principal = auth.getPrincipal();
		if (!(principal instanceof MyUserDetails)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
		}
		return ((MyUserDetails) principal).getUser();
	}
}

