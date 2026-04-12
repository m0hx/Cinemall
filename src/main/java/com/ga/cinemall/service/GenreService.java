package com.ga.cinemall.service;

import com.ga.cinemall.model.Genre;
import com.ga.cinemall.repository.GenreRepository;
import com.ga.cinemall.repository.MovieRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class GenreService {

	private final GenreRepository genreRepository;
	private final MovieRepository movieRepository;

	public List<Genre> getAllGenres() {
		return genreRepository.findAll();
	}

	public Genre getGenreById(Long genreId) {
		return genreRepository
				.findById(genreId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found with id: " + genreId));
	}

	public Genre createGenre(Genre genreObject) {
		genreObject.setId(null);
		String name = normalizeName(genreObject.getName());
		genreRepository
				.findByNameIgnoreCase(name)
				.ifPresent(
						g -> {
							throw new ResponseStatusException(HttpStatus.CONFLICT, "Genre name already exists");
						});
		genreObject.setName(name);
		return genreRepository.save(genreObject);
	}

	public Genre updateGenre(Long genreId, Genre genreObject) {
		Genre existing = getGenreById(genreId);
		String name = normalizeName(genreObject.getName());
		genreRepository
				.findByNameIgnoreCase(name)
				.filter(g -> !g.getId().equals(genreId))
				.ifPresent(
						g -> {
							throw new ResponseStatusException(HttpStatus.CONFLICT, "Genre name already exists");
						});
		existing.setName(name);
		return genreRepository.save(existing);
	}

	public void deleteGenre(Long genreId) {
		getGenreById(genreId);
		if (movieRepository.countByGenre_Id(genreId) > 0) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete genre that is assigned to movies");
		}
		genreRepository.deleteById(genreId);
	}

	private static String normalizeName(String name) {
		if (name == null || name.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Genre name is required");
		}
		return name.trim();
	}
}
