package com.skillbridge.services;

import com.skillbridge.dtos.CategoryRequest;
import com.skillbridge.dtos.CategoryResponse;
import com.skillbridge.entity.Category;
import com.skillbridge.exception.ConflictException;
import com.skillbridge.exception.NotFoundException;
import com.skillbridge.repository.CategoryRepository;
import com.skillbridge.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SkillRepository skillRepository;

    public CategoryService(CategoryRepository categoryRepository, SkillRepository skillRepository) {
        this.categoryRepository = categoryRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        return CategoryResponse.from(getOrThrow(id));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String name = request.name().trim();

        // R-CAT-01 : nom unique
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("Une catégorie porte déjà ce nom");
        }

        Category category = new Category();
        category.setName(name);
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getOrThrow(id);
        String name = request.name().trim();

        // R-CAT-01 : on ignore la catégorie elle-même (on peut la « renommer » avec le même nom)
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new ConflictException("Une catégorie porte déjà ce nom");
        }

        category.setName(name);
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        Category category = getOrThrow(id);

        // R-CAT-02 : on ne supprime pas une catégorie qui contient des compétences
        if (skillRepository.existsByCategoryId(id)) {
            throw new ConflictException("Cette catégorie contient encore des compétences");
        }

        categoryRepository.delete(category);
    }

    private Category getOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Catégorie introuvable"));
    }
}
