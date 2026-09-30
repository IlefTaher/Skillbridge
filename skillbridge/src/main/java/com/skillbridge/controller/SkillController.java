package com.skillbridge.controller;

import com.skillbridge.entity.Skill;
import com.skillbridge.services.SkillService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    // POST /api/skills?categoryId=1
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Skill createSkill(@RequestBody Skill skill, @RequestParam Long categoryId) {
        return skillService.createSkill(skill, categoryId);
    }

    // GET /api/skills
    @GetMapping
    public List<Skill> getAllSkills() {
        return skillService.getAllSkills();
    }

    // GET /api/skills/{id}
    @GetMapping("/{id}")
    public Skill getSkillById(@PathVariable Long id) {
        return skillService.getSkillById(id);
    }

    // GET /api/skills/by-category/{categoryId}
    @GetMapping("/by-category/{categoryId}")
    public List<Skill> getSkillsByCategory(@PathVariable Long categoryId) {
        return skillService.getSkillsByCategory(categoryId);
    }

    // GET /api/skills/search?name=java
    @GetMapping("/search")
    public List<Skill> searchSkills(@RequestParam String name) {
        return skillService.searchSkills(name);
    }

    // PUT /api/skills/{id}?categoryId=1
    @PutMapping("/{id}")
    public Skill updateSkill(@PathVariable Long id, @RequestBody Skill skill, @RequestParam Long categoryId) {
        return skillService.updateSkill(id, skill, categoryId);
    }

    // DELETE /api/skills/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
    }
}