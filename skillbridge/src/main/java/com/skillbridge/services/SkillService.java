package com.skillbridge.services;

import com.skillbridge.entity.Category;
import com.skillbridge.entity.Skill;
import com.skillbridge.repository.CategoryRepository;
import com.skillbridge.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Couche SERVICE pour Skill.
 * Une compétence (Skill) appartient toujours à une Category existante :
 * c'est la règle métier centrale de cette classe.
 */
@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final CategoryRepository categoryRepository; // nécessaire pour vérifier la catégorie

    public SkillService(SkillRepository skillRepository, CategoryRepository categoryRepository) {
        this.skillRepository = skillRepository;
        this.categoryRepository = categoryRepository;
    }

    // ------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------

    @Transactional
    public Skill createSkill(Skill skill, Long categoryId) {
        // Règle 1 : la catégorie indiquée doit exister
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable : " + categoryId));

        // Règle 2 : pas deux compétences avec le même nom
        if (skillRepository.existsByName(skill.getName())) {
            throw new IllegalArgumentException("Cette compétence existe déjà");
        }

        skill.setCategory(category);
        return skillRepository.save(skill);
    }

    // ------------------------------------------------------------------
    // READ
    // ------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Skill getSkillById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compétence introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public List<Skill> getSkillsByCategory(Long categoryId) {
        return skillRepository.findByCategoryId(categoryId);
    }

    @Transactional(readOnly = true)
    public List<Skill> searchSkills(String name) {
        return skillRepository.findByNameContainingIgnoreCase(name);
    }

    // ------------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------------

    @Transactional
    public Skill updateSkill(Long id, Skill updatedSkill, Long categoryId) {
        Skill existing = getSkillById(id);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable : " + categoryId));

        existing.setName(updatedSkill.getName());
        existing.setDescription(updatedSkill.getDescription());
        existing.setCategory(category);
        return skillRepository.save(existing);
    }

    // ------------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------------

    @Transactional
    public void deleteSkill(Long id) {
        Skill existing = getSkillById(id);
        skillRepository.delete(existing);
    }
}
