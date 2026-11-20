package com.staymanager.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class BookingCreateRequest {

    @NotNull
    private Long accommodationId;

    @NotBlank
    @Size(max = 100)
    private String guestName;

    @NotBlank
    @Email
    @Size(max = 254)
    private String guestEmail;

    @NotNull
    @FutureOrPresent
    private LocalDate checkIn;

    @NotNull
    @Future
    private LocalDate checkOut;

    @NotNull
    @Min(1)
    private Integer guestCount;
}
