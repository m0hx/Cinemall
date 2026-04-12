package com.ga.cinemall.service;

import com.ga.cinemall.model.Genre;
import com.ga.cinemall.model.Movie;
import com.ga.cinemall.repository.GenreRepository;
import com.ga.cinemall.repository.MovieRepository;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MovieService {

	private final MovieRepository movieRepository;
	private final GenreRepository genreRepository;

	public List<Movie> getAllMovies() {
		return movieRepository.findAll();
	}

	public Movie getMovieById(Long movieId) {
		return movieRepository
				.findById(movieId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found with id: " + movieId));
	}

	public Movie createMovie(Movie movieObject) {
		movieObject.setId(null);
		if (movieObject.getGenre() == null || movieObject.getGenre().getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Genre is required");
		}
		movieObject.setGenre(resolveGenreById(movieObject.getGenre().getId()));
		return movieRepository.save(movieObject);
	}

	public Movie updateMovie(Long movieId, Movie movieObject) {
		Movie existing = getMovieById(movieId);
		existing.setTitle(movieObject.getTitle());
		existing.setDescription(movieObject.getDescription());
		existing.setReleaseDate(movieObject.getReleaseDate());
		existing.setDurationMins(movieObject.getDurationMins());
		existing.setStatus(movieObject.getStatus());
		if (movieObject.getGenre() != null && movieObject.getGenre().getId() != null) {
			existing.setGenre(resolveGenreById(movieObject.getGenre().getId()));
		}
		movieRepository.save(existing);
		return getMovieById(movieId);
	}

	public void deleteMovie(Long movieId) {
		Movie movie = getMovieById(movieId);
		movieRepository.delete(movie);
	}

	public Movie updatePoster(Long movieId, MultipartFile posterFile) {
		if (posterFile == null || posterFile.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Poster file is required");
		}
		Movie movie = getMovieById(movieId);
		try {
			movie.setPosterName(posterFile.getOriginalFilename());
			String ct = posterFile.getContentType();
			movie.setPosterType(ct != null && !ct.isBlank() ? ct : "application/octet-stream");
			movie.setPoster(posterFile.getBytes());
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read poster file");
		}
		return movieRepository.save(movie);
	}

	private Genre resolveGenreById(Long genreId) {
		return genreRepository
				.findById(genreId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid genre id"));
	}
}
