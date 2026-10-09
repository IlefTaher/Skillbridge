package com.skillbridge.dtos;

import com.skillbridge.entity.Role;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 72) String password, // BCrypt ne lit que 72 octets
        @NotNull Role role,
        @Size(max = 150) String university,
        @Pattern(regexp = "^$|^[+0-9 ]{8,20}$", message = "numéro de téléphone invalide") String phone
) {}

