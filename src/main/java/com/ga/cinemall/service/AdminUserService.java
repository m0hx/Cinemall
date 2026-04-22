package com.ga.cinemall.service;

import com.ga.cinemall.model.Role;
import com.ga.cinemall.model.User;
import com.ga.cinemall.repository.RoleRepository;
import com.ga.cinemall.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AdminUserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;

	public record UserAdminDto(Long id, String email, String displayName, String role, Instant emailVerifiedAt) {}

	public record UpdateUserRequest(String displayName, String role) {}

	@Transactional(readOnly = true)
	public List<UserAdminDto> listUsers() {
		return userRepository.findAllByOrderByIdAsc().stream()
				.map(this::toDto)
				.toList();
	}

	@Transactional(readOnly = true)
	public UserAdminDto getUser(Long id) {
		User u = userRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		return toDto(u);
	}

	@Transactional
	public UserAdminDto updateUser(Long id, UpdateUserRequest req) {
		if (req == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body is required");

		User u = userRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		if (req.displayName() != null) {
			String dn = req.displayName().trim();
			if (dn.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "displayName must not be empty");
			u.setDisplayName(dn);
		}
		if (req.role() != null) {
			String r = req.role().trim().toUpperCase();
			Role role = roleRepository.findByName(r)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown role: " + req.role()));
			u.setRole(role);
		}

		userRepository.save(u);
		return toDto(u);
	}

	private UserAdminDto toDto(User u) {
		return new UserAdminDto(
				u.getId(),
				u.getEmail(),
				u.getDisplayName(),
				u.getRole() != null ? u.getRole().getName() : null,
				u.getEmailVerifiedAt());
	}
}

