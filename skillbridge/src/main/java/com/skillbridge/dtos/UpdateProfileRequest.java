package com.skillbridge.dtos;

import jakarta.validation.constraints.*;

public record UpdateProfileRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @Pattern(regexp = "^$|^[+0-9 ]{8,20}$", message = "numéro de téléphone invalide") String phone,
        @Size(max = 1000) String bio,
        @Size(max = 150) String university
) {}
