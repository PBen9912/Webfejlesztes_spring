package com.staymanager.backend.repository;

import com.staymanager.backend.entity.Booking;
import com.staymanager.backend.entity.BookingStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Provides database access and availability queries for booking entities. */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

  /**
   * Finds all bookings belonging to a specific user.
   *
   * @param userId the user identifier
   * @return the list of bookings belonging to the user
   */
  List<Booking> findByUserId(Long userId);

  /**
   * Checks whether a booking overlaps the specified date range for an accommodation.
   *
   * @param accommodationId the accommodation identifier
   * @param checkIn the requested check-in date
   * @param checkOut the requested check-out date
   * @param status the booking status to check
   * @return true if an overlapping booking exists
   */
  @Query(
      """
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
      @Param("status") BookingStatus status);

  /**
   * Checks whether any booking exists for a specific accommodation.
   *
   * @param accommodationId the accommodation identifier
   * @return true if at least one booking exists
   */
  boolean existsByAccommodationId(Long accommodationId);
}
