package com.skillbridge.services;

import com.skillbridge.entity.User;
import com.skillbridge.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new BadCredentialsException("Not authenticated");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new BadCredentialsException("Unknown user"));
    }

    // To use in the next sprints: requireOwner(offer.getMentor().getId())
    public void requireOwner(Long ownerId) {
        if (!getCurrentUser().getId().equals(ownerId)) {
            throw new AccessDeniedException("This resource does not belong to you");
        }
    }
}