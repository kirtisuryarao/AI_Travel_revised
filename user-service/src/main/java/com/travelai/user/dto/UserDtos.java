package com.travelai.user.dto;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDtos {

    public record RegisterRequest(
        @NotBlank @Size(min = 2, max = 100) String fullName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 100) String password
    ) {}

    public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
    ) {}

    public record UpdateProfileRequest(
        @NotBlank @Size(min = 2, max = 100) String fullName
    ) {}

    public record UserResponse(Long id, String email, String fullName, Set<String> roles) {}

    public record AuthResponse(String token, UserResponse user) {}
}
