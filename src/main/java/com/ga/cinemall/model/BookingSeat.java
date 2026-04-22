package com.ga.cinemall.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
		name = "booking_seats",
		uniqueConstraints = {
			@UniqueConstraint(name = "uk_booking_seat_booking_show_seat", columnNames = { "booking_id", "show_seat_id" }),
			@UniqueConstraint(name = "uk_booking_seat_show_seat", columnNames = { "show_seat_id" }),
		})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingSeat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "booking_id", nullable = false)
	private Booking booking;

	@ManyToOne(optional = false)
	@JoinColumn(name = "show_seat_id", nullable = false)
	private ShowSeat showSeat;
}

