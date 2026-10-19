package com.skillbridge.services;

import com.skillbridge.entity.User;
import com.skillbridge.exception.ConflictException;
import com.skillbridge.exception.NotFoundException;
import com.skillbridge.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * Couche SERVICE : contient la logique métier de SkillBridge pour les utilisateurs.
 *
 * Chemin d'une requête : Controller -> UserService -> UserRepository -> PostgreSQL
 *
 * La création de compte ne passe plus ici : elle est dans AuthService.register
 * (mot de passe chiffré avec BCrypt, rôle ADMIN interdit). Ce service sert
 * uniquement à la zone ADMIN (/api/v1/admin/users).
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ------------------------------------------------------------------
    // READ
    // ------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retourne un utilisateur par son id.
     * NotFoundException -> GlobalExceptionHandler -> 404.
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable : " + id));
    }

    // ------------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------------

    /**
     * Modifie un utilisateur existant.
     * Règles métier :
     *  1. l'utilisateur doit exister (404 sinon) ;
     *  2. le nouvel email ne doit pas appartenir à un AUTRE utilisateur (409 sinon).
     *
     * Le mot de passe, le rôle et enabled ne sont jamais copiés ici.
     */
    @Transactional
    public User updateUser(Long id, User updatedUser) {
        // Règle 1 : l'utilisateur doit exister
        User existing = getUserById(id);

        // Même normalisation que l'inscription et la connexion (R-AUTH-01)
        String email = updatedUser.getEmail().trim().toLowerCase(Locale.ROOT);

        // Règle 2 : le nouvel email ne doit pas appartenir à un AUTRE utilisateur
        userRepository.findByEmail(email)
                .ifPresent(other -> {
                    if (!other.getId().equals(id)) {
                        throw new ConflictException("Cet email est déjà utilisé");
                    }
                });

        // On copie uniquement les champs que l'on a le droit de modifier
        existing.setFirstName(updatedUser.getFirstName());
        existing.setLastName(updatedUser.getLastName());
        existing.setEmail(email);
        existing.setPhone(updatedUser.getPhone());
        existing.setBio(updatedUser.getBio());
        existing.setUniversity(updatedUser.getUniversity());
        existing.setSkills(updatedUser.getSkills());

        return userRepository.save(existing);
    }

    // ------------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------------

    /**
     * Supprime un utilisateur (404 s'il n'existe pas).
     */
    @Transactional
    public void deleteUser(Long id) {
        User existing = getUserById(id);
        userRepository.delete(existing);
    }
}