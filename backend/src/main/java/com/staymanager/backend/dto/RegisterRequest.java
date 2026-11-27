package com.staymanager.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Contains the request data for registering a new user. */
@Data
public class RegisterRequest {
  @NotBlank
  @Size(max = 100)
  private String name;

  @NotBlank
  @Email
  @Size(max = 254)
  private String email;

  @NotBlank
  @Size(min = 8, max = 72)
  private String password;

  private boolean hostRegistration;
}
