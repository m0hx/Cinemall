package com.ga.cinemall.init;

import com.ga.cinemall.model.Genre;
import com.ga.cinemall.model.Hall;
import com.ga.cinemall.model.HallSeat;
import com.ga.cinemall.model.HallStatus;
import com.ga.cinemall.model.Movie;
import com.ga.cinemall.model.MovieStatus;
import com.ga.cinemall.model.Role;
import com.ga.cinemall.model.Showtime;
import com.ga.cinemall.model.ShowtimeStatus;
import com.ga.cinemall.model.SeatType;
import com.ga.cinemall.model.User;
import com.ga.cinemall.repository.GenreRepository;
import com.ga.cinemall.repository.HallRepository;
import com.ga.cinemall.repository.HallSeatRepository;
import com.ga.cinemall.repository.MovieRepository;
import com.ga.cinemall.repository.RoleRepository;
import com.ga.cinemall.repository.ShowtimeRepository;
import com.ga.cinemall.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
			HallRepository hallRepository,
			HallSeatRepository hallSeatRepository,
			MovieRepository movieRepository,
			ShowtimeRepository showtimeRepository,
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

			// seed halls + seats
			seedHallsAndSeats(hallRepository, hallSeatRepository);

			// seed showtimes
			seedShowtimes(movieRepository, hallRepository, showtimeRepository);
		};
	}

	private static void seedGenres(GenreRepository genreRepository) {
		// Keep this list aligned with the movies we seed below.
		List<String> names = List.of(
				"Horror",
				"Mystery",
				"Romance",
				"Adventure",
				"Family",
				"Fantasy",
				"Comedy",
				"Drama");
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
						"Sidney Prescott is forced out of hiding when a new Ghostface targets her family.",
						LocalDate.of(2026, 2, 27),
						114,
						"Horror"),
				new SeedMovie(
						"Titanic",
						"A young aristocrat falls in love with a poor artist aboard the ill-fated RMS Titanic.",
						LocalDate.of(1997, 12, 19),
						195,
						"Romance"),
				new SeedMovie(
						"Harry Potter and the Chamber of Secrets",
						"Harry returns to Hogwarts for his second year as a mysterious force petrifies students.",
						LocalDate.of(2002, 11, 15),
						161,
						"Fantasy"),
				new SeedMovie(
						"Zombieland",
						"Survivors team up across a zombie-filled America with rules, laughs, and chaos.",
						LocalDate.of(2009, 10, 2),
						88,
						"Comedy"),
				new SeedMovie(
						"Gilmore Girls",
						"Small-town mother and daughter navigate school, family, and life in Stars Hollow.",
						LocalDate.of(2000, 10, 5),
						45,
						"Drama"));

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

	private static void seedHallsAndSeats(HallRepository hallRepository, HallSeatRepository hallSeatRepository) {
		Hall hall1 = hallRepository
				.findByNameIgnoreCase("Hall 1")
				.orElseGet(
						() -> {
							Hall h = new Hall();
							h.setName("Hall 1");
							h.setStatus(HallStatus.ACTIVE);
							return hallRepository.save(h);
						});

		if (hallSeatRepository.countByHall_Id(hall1.getId()) > 0) {
			return;
		}

		seedHall1Seats(hall1, hallSeatRepository);
	}

	private static void seedHall1Seats(Hall hall, HallSeatRepository hallSeatRepository) {
		final char firstRow = 'A';
		final char lastRow = 'E';
		final int seatsPerRow = 8;

		for (char row = firstRow; row <= lastRow; row++) {
			for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
				HallSeat seat = new HallSeat();
				seat.setHall(hall);
				seat.setRowLabel(String.valueOf(row));
				seat.setSeatNumber(seatNum);
				seat.setSeatLabel(seat.getRowLabel() + seatNum);
				seat.setAccessible(isHall1AccessibleSeat(row, seatNum));
				seat.setType(hall1SeatType(row));
				hallSeatRepository.save(seat);
			}
		}
	}

	private static boolean isHall1AccessibleSeat(char row, int seatNum) {
		// Hall 1 accessibility: first/last seats in row A.
		return row == 'A' && (seatNum == 1 || seatNum == 8);
	}

	private static SeatType hall1SeatType(char row) {
		if (row == 'E') return SeatType.VIP;
		if (row == 'D') return SeatType.PREMIUM;
		return SeatType.STANDARD;
	}

	private static void seedShowtimes(
			MovieRepository movieRepository,
			HallRepository hallRepository,
			ShowtimeRepository showtimeRepository) {
				Hall hall1 = hallRepository.findByNameIgnoreCase("Hall 1")
						.orElseThrow(() -> new IllegalStateException("Hall 1 not seeded"));

		Movie scream = movieRepository.findByTitleIgnoreCase("Scream 7").orElse(null);
		Movie titanic = movieRepository.findByTitleIgnoreCase("Titanic").orElse(null);
		Movie hp2 = movieRepository.findByTitleIgnoreCase("Harry Potter and the Chamber of Secrets").orElse(null);
		Movie zombieland = movieRepository.findByTitleIgnoreCase("Zombieland").orElse(null);
		Movie gilmoreGirls = movieRepository.findByTitleIgnoreCase("Gilmore Girls").orElse(null);

		if (scream == null || titanic == null || hp2 == null || zombieland == null || gilmoreGirls == null) return;

		// seed showtimes
		// - start tomorrow around 10:00
		// - different number per movie
		// - 30-min gap between showtimes
		// - overlap check remains as a safety net
		Instant cursor = Instant.now()
				.plus(1, ChronoUnit.DAYS)
				.truncatedTo(ChronoUnit.HOURS)
				.plus(10, ChronoUnit.HOURS);

		List<Movie> order = List.of(
				zombieland,
				scream,
				hp2,
				titanic,
				zombieland,
				scream,
				gilmoreGirls,
				hp2,
				zombieland,
				hp2,
				scream,
				titanic,
				zombieland);

		for (Movie m : order) {
			int dur = m.getDurationMins() != null ? m.getDurationMins() : 120;
			Instant startsAt = cursor;
			Instant endsAt = cursor.plus(dur, ChronoUnit.MINUTES);
			Showtime s = new Showtime(null, m, hall1, startsAt, endsAt, ShowtimeStatus.SCHEDULED);

			boolean overlaps = showtimeRepository.existsByHall_IdAndStartsAtLessThanAndEndsAtGreaterThan(
					hall1.getId(),
					s.getEndsAt(),
					s.getStartsAt());
			if (!overlaps) {
				showtimeRepository.save(s);
			}

			cursor = endsAt.plus(30, ChronoUnit.MINUTES);
		}
	}
}
