package com.skillbridge.services;

import com.skillbridge.dtos.SkillResponse;
import com.skillbridge.entity.Role;
import com.skillbridge.entity.Skill;
import com.skillbridge.entity.User;
import com.skillbridge.exception.NotFoundException;
import com.skillbridge.repository.SkillRepository;
import com.skillbridge.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class MentorSkillService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public MentorSkillService(CurrentUserService currentUserService,
                              UserRepository userRepository,
                              SkillRepository skillRepository) {
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<SkillResponse> mySkills() {
        return toResponses(currentMentor());
    }

    @Transactional
    public List<SkillResponse> addSkill(Long skillId) {
        User mentor = currentMentor();

        // R-MSK-03 : la compétence doit exister dans le catalogue
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new NotFoundException("Compétence introuvable"));

        // R-MSK-02 : un Set n'accepte pas de doublon, donc ajouter deux fois ne change rien
        mentor.getSkills().add(skill);
        userRepository.save(mentor);

        return toResponses(mentor);
    }

    @Transactional
    public List<SkillResponse> removeSkill(Long skillId) {
        User mentor = currentMentor();

        // Retirer une compétence qu'on n'a pas ne fait rien (pas d'erreur)
        mentor.getSkills().removeIf(skill -> skill.getId().equals(skillId));
        userRepository.save(mentor);

        return toResponses(mentor);
    }

    // R-MSK-01 : seul un Mentor gère des compétences (protection en plus de la route /mentor/**)
    private User currentMentor() {
        User user = currentUserService.getCurrentUser();
        if (user.getRole() != Role.MENTOR) {
            throw new AccessDeniedException("Réservé aux Mentors");
        }
        return user;
    }

    private List<SkillResponse> toResponses(User mentor) {
        return mentor.getSkills().stream()
                .sorted(Comparator.comparing(Skill::getName))
                .map(SkillResponse::from)
                .toList();
    }
}