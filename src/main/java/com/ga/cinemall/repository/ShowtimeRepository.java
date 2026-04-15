package com.ga.cinemall.repository;

import com.ga.cinemall.model.Showtime;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

	List<Showtime> findByMovie_IdOrderByStartsAtAsc(Long movieId);

	List<Showtime> findByHall_IdOrderByStartsAtAsc(Long hallId);

	boolean existsByHall_IdAndStartsAtLessThanAndEndsAtGreaterThan(Long hallId, Instant endsAtExclusive, Instant startsAtExclusive);

	boolean existsByHall_IdAndIdNotAndStartsAtLessThanAndEndsAtGreaterThan(
			Long hallId,
			Long id,
			Instant endsAtExclusive,
			Instant startsAtExclusive);
}

