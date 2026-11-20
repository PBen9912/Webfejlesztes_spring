package com.staymanager.backend.repository;

import com.staymanager.backend.entity.Booking;
import com.staymanager.backend.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByAccommodation_Id(Long accommodationId);
    List<Booking> findByUser_Id(Long userId);
    List<Booking> findByGuestEmailIgnoreCase(String guestEmail);
    @Query("""
    SELECT COUNT(b) > 0
    FROM Booking b
    WHERE b.accommodation.id = :accommodationId
      AND b.status = :status
      AND b.checkIn < :checkOut
      AND b.checkOut > :checkIn
    """)
    boolean existsOverlappingBooking(
            @Param("accommodationId") Long accommodationId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("status") BookingStatus status
    );
    boolean existsByAccommodation_Id(Long accommodationId);
}
