package com.miempresa.miprimertfg.controller;

import com.miempresa.miprimertfg.model.Difficulty;
import com.miempresa.miprimertfg.model.Question;
import com.miempresa.miprimertfg.model.Theme;
import com.miempresa.miprimertfg.service.QuestionService;
import com.miempresa.miprimertfg.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final ThemeService themeService;

    @GetMapping
    public String listQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long themeId,
            @RequestParam(required = false) String difficulty,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questions;

        // Convert difficulty String to Difficulty enum if present
        Difficulty difficultyEnum = null;
        if (difficulty != null && !difficulty.isEmpty()) {
            try {
                difficultyEnum = Difficulty.valueOf(difficulty.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid difficulty value, ignore it
            }
        }

        if (themeId != null || difficultyEnum != null) {
            questions = questionService.findByFilters(themeId, difficultyEnum, null, pageable);
        } else {
            questions = questionService.findAll(pageable);
        }

        List<Theme> themes = themeService.findAll();

        model.addAttribute("questions", questions);
        model.addAttribute("themes", themes);
        model.addAttribute("selectedTheme", themeId);
        model.addAttribute("selectedDifficulty", difficulty);

        return "questions/list";
    }

    @GetMapping("/test")
    public String testPage(Model model) {
        List<Theme> themes = themeService.findAll();
        model.addAttribute("themes", themes);
        return "questions/test";
    }
}
