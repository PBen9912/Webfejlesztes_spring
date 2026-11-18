package com.staymanager.backend.repository;

import com.staymanager.backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByAccommodation_Id(Long accommodationId);
    List<Booking> findByUser_Id(Long userId);
    List<Booking> findByGuestEmailIgnoreCase(String guestEmail);
}
