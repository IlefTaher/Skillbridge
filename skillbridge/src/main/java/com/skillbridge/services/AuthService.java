package com.skillbridge.services;

import com.skillbridge.dtos.AuthResponse;
import com.skillbridge.dtos.LoginRequest;
import com.skillbridge.dtos.RegisterRequest;
import com.skillbridge.dtos.UserResponse;
import com.skillbridge.entity.Role;
import com.skillbridge.entity.User;
import com.skillbridge.exception.BadRequestException;
import com.skillbridge.exception.ConflictException;
import com.skillbridge.repository.UserRepository;
import com.skillbridge.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // R-AUTH-02: an Admin is never created through public registration
        if (request.role() == Role.ADMIN) {
            throw new BadRequestException("This account type cannot be created through registration");
        }

        String email = normalize(request.email());

        // R-AUTH-01: unique email
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("This email is already in use");
        }

        User user = new User();
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password())); // R-AUTH-03: hashed with BCrypt
        user.setRole(request.role());
        user.setUniversity(request.university());
        user.setPhone(request.phone());
        user.setEnabled(true);

        User saved = userRepository.save(user);
        return buildResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalize(request.email());

        // Throws BadCredentialsException (wrong password) or DisabledException (blocked account)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        return buildResponse(user);
    }

    private AuthResponse buildResponse(User user) {
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return AuthResponse.of(token, UserResponse.from(user));
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}