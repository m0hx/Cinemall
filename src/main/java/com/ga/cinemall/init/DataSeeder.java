package com.ga.cinemall.init;

import com.ga.cinemall.model.Genre;
import com.ga.cinemall.model.Movie;
import com.ga.cinemall.model.MovieStatus;
import com.ga.cinemall.model.Role;
import com.ga.cinemall.model.User;
import com.ga.cinemall.repository.GenreRepository;
import com.ga.cinemall.repository.MovieRepository;
import com.ga.cinemall.repository.RoleRepository;
import com.ga.cinemall.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

// Seed Demo users if empty, then genres and movies.
@Configuration
public class DataSeeder {

	@Bean
	CommandLineRunner seedDatabase(
			GenreRepository genreRepository,
			MovieRepository movieRepository,
			RoleRepository roleRepository,
			UserRepository userRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {

			if (userRepository.count() > 0) {
				System.out.println("Database already seeded. Skipping demo users...");
			} else {
				System.out.println("Starting database seeding (demo users)...");

				Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
				Role userRole = roleRepository.findByName("USER").orElseThrow();

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
				System.out.println("✓ Demo user seeding completed.");
			}

			// seed genres
			seedGenres(genreRepository);

			// seed movies
			seedMovies(genreRepository, movieRepository);
		};
	}

	private static void seedGenres(GenreRepository genreRepository) {
		List<String> names = List.of("Horror", "Comedy", "Drama", "Sci-Fi", "Action");
		for (String name : names) {
			if (genreRepository.findByNameIgnoreCase(name).isEmpty()) {
				genreRepository.save(new Genre(null, name));
			}
		}
	}

	private static void seedMovies(GenreRepository genreRepository, MovieRepository movieRepository) {
		record SeedMovie(String title, String description, LocalDate releaseDate, int durationMins, String genreName) {}

		List<SeedMovie> seeds = List.of(
				new SeedMovie(
						"Scream 7",
						"Ghostface returns to Woodsboro. Meta slasher sequel.",
						LocalDate.of(2026, 3, 13),
						122,
						"Horror"),
				new SeedMovie(
						"The Nice Guys",
						"1970s LA private eyes stumble through a conspiracy.",
						LocalDate.of(2016, 5, 20),
						116,
						"Comedy"),
				new SeedMovie(
						"Dune: Part Two",
						"Paul Atreides unites with Chani and the Fremen.",
						LocalDate.of(2024, 3, 1),
						166,
						"Sci-Fi"));

		for (SeedMovie s : seeds) {
			if (movieRepository.findByTitleIgnoreCase(s.title).isPresent()) {
				continue;
			}
			Genre genre = genreRepository
					.findByNameIgnoreCase(s.genreName)
					.orElseThrow(() -> new IllegalStateException("Genre not seeded: " + s.genreName));
			Movie m = new Movie();
			m.setTitle(s.title);
			m.setDescription(s.description);
			m.setReleaseDate(s.releaseDate);
			m.setDurationMins(s.durationMins);
			m.setStatus(MovieStatus.AVAILABLE);
			m.setGenre(genre);
			movieRepository.save(m);
		}
	}
}
