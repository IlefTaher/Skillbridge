package com.skillbridge.dtos;

import com.skillbridge.entity.Skill;

public record SkillResponse(Long id, String name, Long categoryId, String categoryName) {

    public static SkillResponse from(Skill skill) {
        return new SkillResponse(
                skill.getId(),
                skill.getName(),
                skill.getCategory().getId(),
                skill.getCategory().getName());
    }
}
