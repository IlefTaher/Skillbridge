package com.skillbridge;

import com.skillbridge.security.JwtService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "une-cle-de-test-assez-longue-pour-hs256-1234567890";
    private static final String OTHER_SECRET = "une-autre-cle-de-test-assez-longue-pour-hs256-0987654321";

    @Test
    void creeEtRelitUnJeton() {
        JwtService service = new JwtService(SECRET, 3_600_000);

        String token = service.generateToken("sara@mail.com", "MENTOR");

        assertTrue(service.isValid(token));
        assertEquals("sara@mail.com", service.extractEmail(token));
    }

    @Test
    void refuseUnJetonExpire() {
        JwtService service = new JwtService(SECRET, -1000); // expired as soon as it is created

        String token = service.generateToken("sara@mail.com", "MENTOR");

        assertFalse(service.isValid(token));
    }

    @Test
    void refuseUnJetonSigneAvecUneAutreCle() {
        JwtService emetteur = new JwtService(OTHER_SECRET, 3_600_000);
        JwtService verificateur = new JwtService(SECRET, 3_600_000);

        String token = emetteur.generateToken("sara@mail.com", "MENTOR");

        assertFalse(verificateur.isValid(token));
    }

    @Test
    void refuseUnTexteQuiNEstPasUnJeton() {
        JwtService service = new JwtService(SECRET, 3_600_000);

        assertFalse(service.isValid("n-importe-quoi"));
    }
}