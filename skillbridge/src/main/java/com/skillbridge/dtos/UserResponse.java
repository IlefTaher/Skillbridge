package com.skillbridge.dtos;

import com.skillbridge.entity.Role;
import com.skillbridge.entity.User;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Role role,
        String phone,
        String bio,
        String university
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getRole(), user.getPhone(), user.getBio(), user.getUniversity());
    }
}