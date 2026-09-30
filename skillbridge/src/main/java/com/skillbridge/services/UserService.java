
package com.skillbridge.services;

import com.skillbridge.repository.UserRepository;
import com.skillbridge.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Couche SERVICE : contient la logique métier de SkillBridge pour les utilisateurs.
 *
 * Chemin d'une requête : Controller -> UserService -> UserRepository -> PostgreSQL
 *
 * @Service indique à Spring de créer un objet (bean) de cette classe au démarrage
 * pour pouvoir l'injecter ailleurs (par exemple dans le futur UserController).
 */
@Service
public class UserService {

    // La dépendance dont on a besoin pour accéder à la base de données.
    // "final" : elle est définie une seule fois (dans le constructeur) et ne change plus.
    private final UserRepository userRepository;

    /**
     * Injection de dépendances par constructeur :
     * on ne fait jamais "new UserRepository()", Spring nous fournit l'objet lui-même.
     * (Avec un seul constructeur, @Autowired n'est pas nécessaire.)
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------

    /**
     * Crée un nouvel utilisateur.
     * Règle métier : deux utilisateurs ne peuvent pas avoir le même email.
     *
     * @Transactional : si une erreur survient, tout est annulé (rollback) en base.
     */
    @Transactional
    public User createUser(User user) {
        // existsByEmail est plus léger que findByEmail : on veut seulement
        // savoir si l'email existe, pas récupérer l'utilisateur.
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        // save() fait un INSERT car l'utilisateur n'a pas encore d'id.
        return userRepository.save(user);
    }

    // ------------------------------------------------------------------
    // READ
    // ------------------------------------------------------------------

    /**
     * Retourne tous les utilisateurs.
     * readOnly = true : indique à Spring/Hibernate qu'on ne modifie rien
     * (petite optimisation + intention claire).
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retourne un utilisateur par son id.
     * findById renvoie un Optional<User> : l'utilisateur peut ne pas exister.
     * orElseThrow lève une exception si l'Optional est vide.
     * (En phase 10, on remplacera RuntimeException par UserNotFoundException -> 404.)
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + id));
    }

    // ------------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------------

    /**
     * Modifie un utilisateur existant.
     * Règles métier :
     *  1. l'utilisateur doit exister ;
     *  2. le nouvel email ne doit pas appartenir à un AUTRE utilisateur.
     */
    @Transactional
    public User updateUser(Long id, User updatedUser) {
        // Règle 1 : l'utilisateur doit exister
        User existing = getUserById(id);

        // Règle 2 : le nouvel email ne doit pas appartenir à un AUTRE utilisateur
        userRepository.findByEmail(updatedUser.getEmail())
                .ifPresent(other -> {
                    if (!other.getId().equals(id)) {
                        throw new IllegalArgumentException("Cet email est déjà utilisé");
                    }
                });

        // On copie uniquement les champs que l'utilisateur a le droit de modifier
        existing.setFirstName(updatedUser.getFirstName());
        existing.setLastName(updatedUser.getLastName());
        existing.setEmail(updatedUser.getEmail());
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
     * Supprime un utilisateur.
     * Règle métier : on ne peut supprimer qu'un utilisateur qui existe.
     * (deleteById ne lève pas toujours d'erreur si l'id n'existe pas,
     * donc on vérifie d'abord via getUserById.)
     */
    @Transactional
    public void deleteUser(Long id) {
        User existing = getUserById(id);
        userRepository.delete(existing);
    }
}

