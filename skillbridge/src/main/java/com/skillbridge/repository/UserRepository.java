package com.skillbridge.repository;
import com.skillbridge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // « au moins un utilisateur possède-t-il cette compétence ? »
    boolean existsBySkills_Id(Long skillId);
}