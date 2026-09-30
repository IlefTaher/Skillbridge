package com.skillbridge.repository;

import com.skillbridge.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    // Toutes les compétences d'une catégorie donnée
    List<Skill> findByCategoryId(Long categoryId);

    // Recherche partielle, insensible à la casse (utile pour une barre de recherche)
    List<Skill> findByNameContainingIgnoreCase(String name);

    // Règle métier : empêcher les doublons de nom
    boolean existsByName(String name);
}