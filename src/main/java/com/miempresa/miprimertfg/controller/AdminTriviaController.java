package com.miempresa.miprimertfg.controller;

import com.miempresa.miprimertfg.model.Difficulty;
import com.miempresa.miprimertfg.model.Theme;
import com.miempresa.miprimertfg.service.ThemeService;
import com.miempresa.miprimertfg.service.TriviaApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/trivia")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminTriviaController {

    private final TriviaApiService triviaApiService;
    private final ThemeService themeService;

    @GetMapping
    public String showImportForm(Model model) {
        List<Theme> themes = themeService.findAll();
        model.addAttribute("themes", themes);
        model.addAttribute("difficulties", Difficulty.values());
        return "admin/trivia-import";
    }

    @PostMapping("/import")
    public String importQuestions(
            @RequestParam int amount,
            @RequestParam Long themeId,
            @RequestParam(required = false) String difficulty,
            RedirectAttributes redirectAttributes) {

        try {
            int count = triviaApiService.importQuestions(amount, themeId, difficulty);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Se han importado " + count + " preguntas correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al importar preguntas: " + e.getMessage());
        }

        return "redirect:/admin/trivia";
    }
}
