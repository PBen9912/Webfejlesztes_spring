package com.staymanager.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Represents a bookable accommodation owned by a host. */
@Entity
@Table(name = "accommodations")
@Getter
@Setter
@NoArgsConstructor
public class Accommodation {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  private User owner;

  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Column(name = "description", nullable = true, columnDefinition = "TEXT")
  private String description;

  @Column(name = "city", length = 100, nullable = false)
  private String city;

  @Column(name = "address", length = 200, nullable = false)
  private String address;

  @Column(name = "capacity", nullable = false)
  private Integer capacity;

  @Column(name = "price_per_night", nullable = false, precision = 12, scale = 2)
  private BigDecimal pricePerNight;
}
