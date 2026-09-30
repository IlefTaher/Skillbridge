
package com.skillbridge.controller;

import com.skillbridge.entity.User;
import com.skillbridge.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
 * @RestController = @Controller + @ResponseBody :
 *   Spring transforme automatiquement les objets Java retournés en JSON.
 *
 * @RequestMapping("/api/users") : préfixe commun de toutes les routes ci-dessous.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    // Le controller ne parle qu'au SERVICE, jamais directement au Repository.
    private final UserService userService;

    // Injection par constructeur : Spring fournit le bean UserService.
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST /api/users
     * @RequestBody : Spring convertit le JSON reçu en objet Java User.
     * @ResponseStatus(CREATED) : renvoie le code HTTP 201 (créé) au lieu de 200.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    /**
     * GET /api/users
     * Retourne la liste de tous les utilisateurs.
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * GET /api/users/{id}
     * @PathVariable : récupère la valeur {id} présente dans l'URL.
     * Exemple : GET /api/users/3 -> id = 3
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    /**
     * PUT /api/users/{id}
     * Modifie l'utilisateur d'id {id} avec les données du JSON reçu.
     */
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    /**
     * DELETE /api/users/{id}
     * @ResponseStatus(NO_CONTENT) : renvoie 204 (succès, pas de corps de réponse).
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
