package com.ga.cinemall.repository;

import com.ga.cinemall.model.Hall;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HallRepository extends JpaRepository<Hall, Long> {

	Optional<Hall> findByNameIgnoreCase(String name);
}

