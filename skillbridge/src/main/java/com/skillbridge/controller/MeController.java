package com.skillbridge.controller;

import com.skillbridge.dtos.UpdateProfileRequest;
import com.skillbridge.dtos.UserResponse;
import com.skillbridge.services.ProfileService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me")
public class MeController {

    private final ProfileService profileService;

    public MeController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public UserResponse me() {
        return profileService.getMyProfile();
    }

    @PutMapping
    public UserResponse update(@Valid @RequestBody UpdateProfileRequest request) {
        return profileService.updateMyProfile(request);
    }
}