package com.skillbridge.services;

import com.skillbridge.dtos.UpdateProfileRequest;
import com.skillbridge.dtos.UserResponse;
import com.skillbridge.entity.User;
import com.skillbridge.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    public ProfileService(CurrentUserService currentUserService, UserRepository userRepository) {
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
    }

    public UserResponse getMyProfile() {
        return UserResponse.from(currentUserService.getCurrentUser());
    }

    @Transactional
    public UserResponse updateMyProfile(UpdateProfileRequest request) {
        User user = currentUserService.getCurrentUser();
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setPhone(request.phone());
        user.setBio(request.bio());
        user.setUniversity(request.university());
        return UserResponse.from(userRepository.save(user));
    }
}