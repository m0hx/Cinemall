package com.ga.cinemall.repository;

import com.ga.cinemall.model.Movie;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {

	Optional<Movie> findByTitleIgnoreCase(String title);

	long countByGenre_Id(Long genreId);
}
