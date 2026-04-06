package com.ga.cinemall.controller;

import com.ga.cinemall.model.Movie;
import com.ga.cinemall.service.MovieService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

	private final MovieService movieService;

	@PostMapping
	@PreAuthorize("hasAuthority('ADMIN')")
	public Movie createMovie(@RequestBody Movie movieObject) {
		return movieService.createMovie(movieObject);
	}

	@GetMapping
	public List<Movie> getAllMovies() {
		return movieService.getAllMovies();
	}

	@GetMapping("/{movieId}")
	public Movie getMovieById(@PathVariable Long movieId) {
		return movieService.getMovieById(movieId);
	}

	@GetMapping("/{movieId}/poster")
	public ResponseEntity<byte[]> getPoster(@PathVariable Long movieId) {
		Movie m = movieService.getMovieById(movieId);
		if (m.getPoster() == null || m.getPoster().length == 0) {
			return ResponseEntity.notFound().build();
		}
		String type = m.getPosterType() != null ? m.getPosterType() : "image/jpeg";
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, type).body(m.getPoster());
	}

	@PutMapping("/{movieId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public Movie updateMovie(@PathVariable Long movieId, @RequestBody Movie movieObject) {
		return movieService.updateMovie(movieId, movieObject);
	}

	@PutMapping(value = "/{movieId}/poster", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasAuthority('ADMIN')")
	public Movie updatePoster(@PathVariable Long movieId, @RequestParam("poster") MultipartFile poster) {
		return movieService.updatePoster(movieId, poster);
	}

	@DeleteMapping("/{movieId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public void deleteMovie(@PathVariable Long movieId) {
		movieService.deleteMovie(movieId);
	}
}
