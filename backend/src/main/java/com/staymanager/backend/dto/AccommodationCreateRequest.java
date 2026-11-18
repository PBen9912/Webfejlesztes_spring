package com.staymanager.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

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
