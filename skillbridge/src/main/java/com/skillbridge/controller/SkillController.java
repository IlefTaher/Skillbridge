package com.skillbridge.controller;

import com.skillbridge.dtos.SkillResponse;
import com.skillbridge.services.SkillService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    // GET /api/v1/skills            -> toutes les compétences
    // GET /api/v1/skills?categoryId=2 -> celles d'une catégorie
    @GetMapping
    public List<SkillResponse> list(@RequestParam(required = false) Long categoryId) {
        return skillService.findAll(categoryId);
    }

    @GetMapping("/{id}")
    public SkillResponse get(@PathVariable Long id) {
        return skillService.findById(id);
    }
}