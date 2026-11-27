package com.staymanager.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

/** Contains the request data for creating or updating an accommodation. */
@Data
public class AccommodationCreateRequest {
  @NotBlank
  @Size(max = 100)
  private String name;

  private String description;

  @NotBlank
  @Size(max = 100)
  private String city;

  @NotBlank
  @Size(max = 200)
  private String address;

  @NotNull
  @Min(1)
  private Integer capacity;

  @NotNull
  @DecimalMin("0.01")
  private BigDecimal pricePerNight;
}
