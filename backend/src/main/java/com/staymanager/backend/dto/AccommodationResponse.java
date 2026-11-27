package com.staymanager.backend.dto;

import java.math.BigDecimal;
import lombok.Data;

/** Represents accommodation data returned by the REST API. */
@Data
public class AccommodationResponse {
  private Long id;
  private String name;
  private String description;
  private String city;
  private String address;
  private Integer capacity;
  private BigDecimal pricePerNight;
  private Long ownerId;
}
