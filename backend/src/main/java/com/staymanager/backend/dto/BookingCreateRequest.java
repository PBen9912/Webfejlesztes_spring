package com.staymanager.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Contains the request data for creating a booking. */
@Getter
@Setter
@NoArgsConstructor
public class BookingCreateRequest {

  @NotNull private Long accommodationId;

  @NotBlank
  @Size(max = 100)
  private String guestName;

  @NotBlank
  @Email
  @Size(max = 254)
  private String guestEmail;

  @NotNull @FutureOrPresent private LocalDate checkIn;

  @NotNull @Future private LocalDate checkOut;

  @NotNull
  @Min(1)
  private Integer guestCount;
}
