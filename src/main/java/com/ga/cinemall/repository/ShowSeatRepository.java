package com.ga.cinemall.repository;

import com.ga.cinemall.model.ShowSeat;
import com.ga.cinemall.model.ShowSeatStatus;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {

	long countByShowtime_Id(Long showtimeId);

	boolean existsByShowtime_IdAndHallSeat_Id(Long showtimeId, Long hallSeatId);

	List<ShowSeat> findByShowtime_IdOrderByHallSeat_RowLabelAscHallSeat_SeatNumberAsc(Long showtimeId);

	List<ShowSeat> findByShowtime_IdAndIdIn(Long showtimeId, List<Long> ids);

	long countByShowtime_IdAndReservedByUser_IdAndStatusAndReservedUntilAfter(
			Long showtimeId,
			Long reservedByUserId,
			ShowSeatStatus status,
			Instant reservedUntilAfter);
}

