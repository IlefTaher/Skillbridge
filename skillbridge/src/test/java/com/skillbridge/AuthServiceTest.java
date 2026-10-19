package com.skillbridge;

import com.skillbridge.dtos.RegisterRequest;
import com.skillbridge.entity.Role;
import com.skillbridge.entity.User;
import com.skillbridge.exception.BadRequestException;
import com.skillbridge.exception.ConflictException;
import com.skillbridge.repository.UserRepository;
import com.skillbridge.security.JwtService;
import com.skillbridge.services.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtService jwtService;

    @InjectMocks
    AuthService authService;

    private RegisterRequest request(Role role) {
        return new RegisterRequest("Sara", "Ben", "Sara@Mail.com", "motdepasse123", role, "ESPRIT", "+21650123456");
    }

    @Test
    void register_refuseLeRoleAdmin() { // R-AUTH-02
        assertThrows(BadRequestException.class, () -> authService.register(request(Role.ADMIN)));

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_refuseUnEmailDejaUtilise() { // R-AUTH-01
        when(userRepository.existsByEmail("sara@mail.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(request(Role.STUDENT)));

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_chiffreLeMotDePasseEtNormaliseLEmail() { // R-AUTH-03
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("motdepasse123")).thenReturn("EMPREINTE");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(any(), any())).thenReturn("jeton");

        authService.register(request(Role.MENTOR));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("EMPREINTE", captor.getValue().getPassword());
        assertEquals("sara@mail.com", captor.getValue().getEmail());
        assertEquals(Role.MENTOR, captor.getValue().getRole());
    }
}