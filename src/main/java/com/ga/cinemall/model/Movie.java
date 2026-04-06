package com.ga.cinemall.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 255)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;

	@Column(name = "release_date", nullable = false)
	private LocalDate releaseDate;

	@Column(name = "duration_mins", nullable = false)
	private Integer durationMins;

	@Column(name = "poster_name", length = 255)
	private String posterName;

	@Column(name = "poster_type", length = 127)
	private String posterType;

	@JsonIgnore
	@Column(name = "poster", columnDefinition = "BYTEA")
	private byte[] poster;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private MovieStatus status;
}
