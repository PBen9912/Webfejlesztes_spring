package com.staymanager.backend.dto;

import com.staymanager.backend.entity.Role;
import lombok.Data;

/** Represents user data returned by the REST API. */
@Data
public class UserResponse {
  private Long id;
  private String name;
  private String email;
  private Role role;
}
