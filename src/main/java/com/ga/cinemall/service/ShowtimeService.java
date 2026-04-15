package com.ga.cinemall.service;

import com.ga.cinemall.model.Hall;
import com.ga.cinemall.model.Movie;
import com.ga.cinemall.model.Showtime;
import com.ga.cinemall.model.ShowtimeStatus;
import com.ga.cinemall.repository.HallRepository;
import com.ga.cinemall.repository.MovieRepository;
import com.ga.cinemall.repository.ShowtimeRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ShowtimeService {

	private final ShowtimeRepository showtimeRepository;
	private final MovieRepository movieRepository;
	private final HallRepository hallRepository;

	public List<Showtime> getAllShowtimes() {
		return showtimeRepository.findAll();
	}

	public Showtime getShowtimeById(Long showtimeId) {
		return showtimeRepository
				.findById(showtimeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Showtime not found with id: " + showtimeId));
	}

	public List<Showtime> getShowtimesForMovie(Long movieId) {
		requireMovie(movieId);
		return showtimeRepository.findByMovie_IdOrderByStartsAtAsc(movieId);
	}

	public Showtime createShowtime(Showtime showtimeObject) {
		showtimeObject.setId(null);

		if (showtimeObject.getMovie() == null || showtimeObject.getMovie().getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie is required");
		}
		if (showtimeObject.getHall() == null || showtimeObject.getHall().getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hall is required");
		}
		if (showtimeObject.getStartsAt() == null || showtimeObject.getEndsAt() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "startsAt and endsAt are required");
		}
		if (!showtimeObject.getEndsAt().isAfter(showtimeObject.getStartsAt())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endsAt must be after startsAt");
		}

		Movie movie = requireMovie(showtimeObject.getMovie().getId());
		Hall hall = requireHall(showtimeObject.getHall().getId());
		showtimeObject.setMovie(movie);
		showtimeObject.setHall(hall);
		if (showtimeObject.getStatus() == null) {
			showtimeObject.setStatus(ShowtimeStatus.SCHEDULED);
		}

		ensureNoOverlap(hall.getId(), showtimeObject.getStartsAt(), showtimeObject.getEndsAt(), null);
		return showtimeRepository.save(showtimeObject);
	}

	public Showtime updateShowtime(Long showtimeId, Showtime showtimeObject) {
		Showtime existing = getShowtimeById(showtimeId);

		Instant startsAt = showtimeObject.getStartsAt() != null ? showtimeObject.getStartsAt() : existing.getStartsAt();
		Instant endsAt = showtimeObject.getEndsAt() != null ? showtimeObject.getEndsAt() : existing.getEndsAt();
		if (!endsAt.isAfter(startsAt)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endsAt must be after startsAt");
		}

		if (showtimeObject.getMovie() != null && showtimeObject.getMovie().getId() != null) {
			existing.setMovie(requireMovie(showtimeObject.getMovie().getId()));
		}
		if (showtimeObject.getHall() != null && showtimeObject.getHall().getId() != null) {
			existing.setHall(requireHall(showtimeObject.getHall().getId()));
		}
		existing.setStartsAt(startsAt);
		existing.setEndsAt(endsAt);
		if (showtimeObject.getStatus() != null) {
			existing.setStatus(showtimeObject.getStatus());
		}

		ensureNoOverlap(existing.getHall().getId(), existing.getStartsAt(), existing.getEndsAt(), existing.getId());
		return showtimeRepository.save(existing);
	}

	public void deleteShowtime(Long showtimeId) {
		Showtime existing = getShowtimeById(showtimeId);
		showtimeRepository.delete(existing);
	}

	private Movie requireMovie(Long movieId) {
		return movieRepository
				.findById(movieId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found with id: " + movieId));
	}

	private Hall requireHall(Long hallId) {
		return hallRepository
				.findById(hallId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hall not found with id: " + hallId));
	}

	private void ensureNoOverlap(Long hallId, Instant startsAt, Instant endsAt, Long ignoreShowtimeId) {
		boolean overlaps = ignoreShowtimeId == null
				? showtimeRepository.existsByHall_IdAndStartsAtLessThanAndEndsAtGreaterThan(hallId, endsAt, startsAt)
				: showtimeRepository.existsByHall_IdAndIdNotAndStartsAtLessThanAndEndsAtGreaterThan(
						hallId,
						ignoreShowtimeId,
						endsAt,
						startsAt);

		if (overlaps) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Showtime overlaps with another showtime in this hall");
		}
	}
}

