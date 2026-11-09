package com.skillbridge.repository;

import com.skillbridge.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);

    // « existe-t-il une AUTRE catégorie avec ce nom ? » (utile pour une modification)
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}