package com.skillbridge.services;

import com.skillbridge.dtos.SkillRequest;
import com.skillbridge.dtos.SkillResponse;
import com.skillbridge.entity.Category;
import com.skillbridge.entity.Skill;
import com.skillbridge.exception.ConflictException;
import com.skillbridge.exception.NotFoundException;
import com.skillbridge.repository.CategoryRepository;
import com.skillbridge.repository.SkillRepository;
import com.skillbridge.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public SkillService(SkillRepository skillRepository,
                        CategoryRepository categoryRepository,
                        UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // categoryId est facultatif : s'il est absent, on renvoie toutes les compétences
    @Transactional(readOnly = true)
    public List<SkillResponse> findAll(Long categoryId) {
        List<Skill> skills = (categoryId == null)
                ? skillRepository.findAllByOrderByNameAsc()
                : skillRepository.findByCategoryIdOrderByNameAsc(categoryId);
        return skills.stream().map(SkillResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SkillResponse findById(Long id) {
        return SkillResponse.from(getOrThrow(id));
    }

    @Transactional
    public SkillResponse create(SkillRequest request) {
        // R-SKILL-02 : la catégorie doit exister (sinon 404)
        Category category = findCategory(request.categoryId());
        String name = request.name().trim();

        // R-SKILL-01 : nom unique
        if (skillRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("Une compétence porte déjà ce nom");
        }

        Skill skill = new Skill();
        skill.setName(name);
        skill.setCategory(category);
        return SkillResponse.from(skillRepository.save(skill));
    }

    @Transactional
    public SkillResponse update(Long id, SkillRequest request) {
        Skill skill = getOrThrow(id);
        Category category = findCategory(request.categoryId());
        String name = request.name().trim();

        if (skillRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new ConflictException("Une compétence porte déjà ce nom");
        }

        skill.setName(name);
        skill.setCategory(category);
        return SkillResponse.from(skillRepository.save(skill));
    }

    @Transactional
    public void delete(Long id) {
        Skill skill = getOrThrow(id);

        // R-SKILL-03 : on ne supprime pas une compétence qu'un Mentor possède
        if (userRepository.existsBySkills_Id(id)) {
            throw new ConflictException("Cette compétence est utilisée par des Mentors");
        }

        skillRepository.delete(skill);
    }

    private Skill getOrThrow(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compétence introuvable"));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Catégorie introuvable"));
    }
}