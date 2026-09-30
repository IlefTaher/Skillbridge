package com.skillbridge.repository;

import com.skillbridge.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Règle métier "nom unique" : utile pour empêcher les doublons de catégorie
    boolean existsByName(String name);
}