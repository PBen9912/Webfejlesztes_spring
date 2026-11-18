package com.staymanager.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

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
