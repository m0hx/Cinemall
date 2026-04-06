package com.ga.cinemall.init;

import com.ga.cinemall.model.Role;
import com.ga.cinemall.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer {

	private final RoleRepository roleRepository;

	@PostConstruct
	public void init() {
		if (roleRepository.findByName("USER").isEmpty()) {
			roleRepository.save(new Role(null, "USER"));
		}
		if (roleRepository.findByName("ADMIN").isEmpty()) {
			roleRepository.save(new Role(null, "ADMIN"));
		}
	}
}
