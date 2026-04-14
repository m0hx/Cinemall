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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
		name = "hall_seats",
		uniqueConstraints = {
			@UniqueConstraint(name = "uk_hall_seat_label", columnNames = { "hall_id", "seat_label" }),
		})
@Getter
@Setter
@NoArgsConstructor
public class HallSeat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "hall_id", nullable = false)
	private Hall hall;

	@Column(name = "row_label", nullable = false, length = 8)
	private String rowLabel;

	@Column(name = "seat_number", nullable = false)
	private Integer seatNumber;

	@Column(name = "seat_label", nullable = false, length = 16)
	private String seatLabel;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private SeatType type;

	@Column(name = "is_accessible", nullable = false)
	private boolean isAccessible;
}

