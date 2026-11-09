package com.skillbridge.controller;

import com.skillbridge.dtos.SkillResponse;
import com.skillbridge.services.MentorSkillService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mentor/skills")
public class MentorSkillController {

    private final MentorSkillService mentorSkillService;

    public MentorSkillController(MentorSkillService mentorSkillService) {
        this.mentorSkillService = mentorSkillService;
    }

    @GetMapping
    public List<SkillResponse> mySkills() {
        return mentorSkillService.mySkills();
    }

    @PostMapping("/{skillId}")
    public List<SkillResponse> add(@PathVariable Long skillId) {
        return mentorSkillService.addSkill(skillId);
    }

    @DeleteMapping("/{skillId}")
    public List<SkillResponse> remove(@PathVariable Long skillId) {
        return mentorSkillService.removeSkill(skillId);
    }
}
