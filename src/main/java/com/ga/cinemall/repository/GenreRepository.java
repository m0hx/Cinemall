package com.ga.cinemall.repository;

import com.ga.cinemall.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    Optional<Genre> findByNameIgnoreCase(String name);

}
