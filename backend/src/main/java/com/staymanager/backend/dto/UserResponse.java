package com.staymanager.backend.dto;

import com.staymanager.backend.entity.Role;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
}
