package com.staymanager.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Contains user credentials required for authentication. */
@Data
public class LoginRequest {
  @NotBlank @Email private String email;
  @NotBlank private String password;
}
