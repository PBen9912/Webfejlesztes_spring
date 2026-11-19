package com.staymanager.backend.controller;

import com.staymanager.backend.dto.LoginRequest;
import com.staymanager.backend.dto.RegisterRequest;
import com.staymanager.backend.dto.UserResponse;
import com.staymanager.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request){
        UserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request){
        userService.login(request);
        return ResponseEntity.ok().build();
    }
}
