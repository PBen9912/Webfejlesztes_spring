package com.staymanager.backend.dto;

/** Represents a successful authentication response containing a JWT. */
public record LoginResponse(String token) {}
