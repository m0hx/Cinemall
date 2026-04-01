package com.ga.cinemall.init;

import com.ga.cinemall.model.Role;
import com.ga.cinemall.model.User;
import com.ga.cinemall.repository.RoleRepository;
import com.ga.cinemall.repository.UserRepository;
import java.time.Instant;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Demo users only if the {@code users} table is empty. Roles are created by {@link RoleInitializer}.
 */
@Configuration
public class DataSeeder {

	@Bean
	CommandLineRunner seedDatabase(
			RoleRepository roleRepository,
			UserRepository userRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.count() > 0) {
				System.out.println("Database already seeded. Skipping demo users...");
				return;
			}

			System.out.println("Starting database seeding (demo users)...");

			Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
			Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow();

			User admin = new User();
			admin.setEmail("admin@cinemall.local");
			admin.setPasswordHash(passwordEncoder.encode("admin123"));
			admin.setDisplayName("Admin");
			admin.setRole(adminRole);
			admin.setEmailVerifiedAt(Instant.now());
			userRepository.save(admin);

			User demo = new User();
			demo.setEmail("demo@cinemall.local");
			demo.setPasswordHash(passwordEncoder.encode("demo123"));
			demo.setDisplayName("Demo User");
			demo.setRole(userRole);
			demo.setEmailVerifiedAt(Instant.now());
			userRepository.save(demo);

			System.out.println("✓ Admin: admin@cinemall.local / admin123");
			System.out.println("✓ Demo:  demo@cinemall.local / demo123");
			System.out.println("✓ Database seeding completed.");
		};
	}
}
