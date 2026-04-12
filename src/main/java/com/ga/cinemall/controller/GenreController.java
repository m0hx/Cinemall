package com.ga.cinemall.controller;

import com.ga.cinemall.model.Genre;
import com.ga.cinemall.service.GenreService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

	private final GenreService genreService;

	@GetMapping
	public List<Genre> getAllGenres() {
		return genreService.getAllGenres();
	}

	@GetMapping("/{genreId}")
	public Genre getGenreById(@PathVariable Long genreId) {
		return genreService.getGenreById(genreId);
	}

	@PostMapping
	@PreAuthorize("hasAuthority('ADMIN')")
	public Genre createGenre(@RequestBody Genre genreObject) {
		return genreService.createGenre(genreObject);
	}

	@PutMapping("/{genreId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public Genre updateGenre(@PathVariable Long genreId, @RequestBody Genre genreObject) {
		return genreService.updateGenre(genreId, genreObject);
	}

	@DeleteMapping("/{genreId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public void deleteGenre(@PathVariable Long genreId) {
		genreService.deleteGenre(genreId);
	}
}
