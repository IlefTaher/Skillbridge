package com.skillbridge.controller;

import com.skillbridge.dtos.UserResponse;
import com.skillbridge.entity.User;
import com.skillbridge.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Couche CONTROLLER : expose le UserService sous forme d'API REST.
 * Elle ne contient AUCUNE logique métier : elle reçoit la requête HTTP,
 * appelle le service, et renvoie la réponse en JSON.
 *
 * Zone ADMIN : le chemin /api/v1/admin/** est réservé au rôle ADMIN
 * (règle définie dans SecurityConfig).
 * La création d'utilisateur ne passe plus ici : elle se fait par
 * POST /api/v1/auth/register (mot de passe chiffré, rôle contrôlé).
 */
@RestController
@RequestMapping("/api/v1/admin/users")   // avant : "/api/users"
public class UserController {

    // Le controller ne parle qu'au SERVICE, jamais directement au Repository.
    private final UserService userService;

    // Injection par constructeur : Spring fournit le bean UserService.
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /api/v1/admin/users
     * Retourne la liste de tous les utilisateurs, sans mot de passe (UserResponse).
     */
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(UserResponse::from)
                .toList();
    }

    /**
     * GET /api/v1/admin/users/{id}
     * @PathVariable : récupère la valeur {id} présente dans l'URL.
     */
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return UserResponse.from(userService.getUserById(id));
    }

    /**
     * PUT /api/v1/admin/users/{id}
     * Modifie l'utilisateur d'id {id} avec les données du JSON reçu.
     */
    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody User user) {
        return UserResponse.from(userService.updateUser(id, user));
    }

    /**
     * DELETE /api/v1/admin/users/{id}
     * @ResponseStatus(NO_CONTENT) : renvoie 204 (succès, pas de corps de réponse).
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}