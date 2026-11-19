package com.staymanager.backend.service;

import com.staymanager.backend.dto.RegisterRequest;
import com.staymanager.backend.dto.UserResponse;
import com.staymanager.backend.entity.Role;
import com.staymanager.backend.entity.User;
import com.staymanager.backend.exception.EmailAlreadyRegisteredException;
import com.staymanager.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserResponse register(RegisterRequest request){
        User user = new User();
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if(userRepository.findByEmail(email).isPresent()){
            throw new EmailAlreadyRegisteredException("Email already registered");
        }
        user.setEmail(email);
        if(request.getPassword().getBytes(StandardCharsets.UTF_8).length > 72){
            throw new IllegalArgumentException("Password exceeds 72 bytes");
        }
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashedPassword);
        user.setName(request.getName());
        if(request.isHostRegistration()){
            user.setRole(Role.HOST);
        }
        else{
            user.setRole(Role.USER);
        }
        return toResponse(userRepository.save(user));
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole());
        response.setId(user.getId());
        return response;
    }
}
