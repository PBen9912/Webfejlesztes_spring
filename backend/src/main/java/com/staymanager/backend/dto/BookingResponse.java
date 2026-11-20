package com.staymanager.backend.dto;

import com.staymanager.backend.entity.BookingStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class BookingResponse {
    private Long id;
    private Long accommodationId;
    private Long userId;
    private String guestName;
    private String guestEmail;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer guestCount;
    private BigDecimal totalPrice;
    private BookingStatus status;
}
