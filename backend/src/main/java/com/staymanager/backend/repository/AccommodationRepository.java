package com.staymanager.backend.repository;

import com.staymanager.backend.entity.Accommodation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Provides database access for accommodation entities. */
@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

  /**
   * Finds accommodations by city, ignoring case differences.
   *
   * @param city the city name
   * @return the list of matching accommodations
   */
  List<Accommodation> findByCityIgnoreCase(String city);
}
