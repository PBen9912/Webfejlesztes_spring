package com.staymanager.backend.repository;

import com.staymanager.backend.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Provides database access for registered user entities. */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Finds a registered user by email address.
   *
   * @param email the user's email address
   * @return an optional containing the user if found, or an empty optional otherwise
   */
  Optional<User> findByEmail(String email);
}
