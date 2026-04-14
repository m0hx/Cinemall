package com.ga.cinemall.repository;

import com.ga.cinemall.model.HallSeat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HallSeatRepository extends JpaRepository<HallSeat, Long> {

	List<HallSeat> findByHall_IdOrderByRowLabelAscSeatNumberAsc(Long hallId);

	boolean existsByHall_IdAndSeatLabel(Long hallId, String seatLabel);

	long countByHall_Id(Long hallId);
}

