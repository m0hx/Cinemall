package com.ga.cinemall.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
		name = "show_seats",
		uniqueConstraints = {
			@UniqueConstraint(name = "uk_show_seat_showtime_hall_seat", columnNames = { "showtime_id", "hall_seat_id" }),
		})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowSeat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "showtime_id", nullable = false)
	private Showtime showtime;

	@ManyToOne(optional = false)
	@JoinColumn(name = "hall_seat_id", nullable = false)
	private HallSeat hallSeat;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private ShowSeatStatus status;

	@ManyToOne
	@JoinColumn(name = "reserved_by_user_id")
	private User reservedByUser;

	@Column(name = "reserved_until")
	private Instant reservedUntil;
}
