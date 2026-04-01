package com.ga.cinemall.init;

import com.ga.cinemall.model.Role;
import com.ga.cinemall.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Ensures reference roles exist before {@link DataSeeder} and the rest of the app run.
 */
@Component
@RequiredArgsConstructor
public class RoleInitializer {

	private final RoleRepository roleRepository;

	@PostConstruct
	public void init() {
		if (roleRepository.findByName("ROLE_USER").isEmpty()) {
			roleRepository.save(new Role(null, "ROLE_USER"));
		}
		if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
			roleRepository.save(new Role(null, "ROLE_ADMIN"));
		}
	}
}
