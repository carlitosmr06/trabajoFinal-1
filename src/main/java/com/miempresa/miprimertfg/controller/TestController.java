package com.miempresa.miprimertfg.controller;

import com.miempresa.miprimertfg.model.Difficulty;
import com.miempresa.miprimertfg.model.Question;
import com.miempresa.miprimertfg.model.TestResult;
import com.miempresa.miprimertfg.model.Theme;
import com.miempresa.miprimertfg.model.User;
import com.miempresa.miprimertfg.service.QuestionService;
import com.miempresa.miprimertfg.service.TestService;
import com.miempresa.miprimertfg.service.ThemeService;
import com.miempresa.miprimertfg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final ThemeService themeService;
    private final QuestionService questionService;
    private final TestService testService;
    private final UserService userService;

    @GetMapping
    public String testPage(Model model) {
        List<Theme> themes = themeService.findAll();
        model.addAttribute("themes", themes);
        return "test/test";
    }

    @PostMapping("/start")
    public String startTest(
            @RequestParam(required = false) Long themeId,
            @RequestParam(required = false) String difficulty,
            @RequestParam(defaultValue = "10") int questionCount,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            // Convert difficulty String to Difficulty enum if present
            Difficulty difficultyEnum = null;
            if (difficulty != null && !difficulty.isEmpty()) {
                try {
                    difficultyEnum = Difficulty.valueOf(difficulty.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // Invalid difficulty value, ignore it
                }
            }

            // Get random questions based on filters
            List<Question> questions;
            if (themeId != null && difficultyEnum != null) {
                // Both filters - for now just use theme filter
                questions = questionService.getRandomQuestionsByTheme(themeId, questionCount);
            } else if (themeId != null) {
                // Only theme filter
                questions = questionService.getRandomQuestionsByTheme(themeId, questionCount);
            } else if (difficultyEnum != null) {
                // Only difficulty filter
                questions = questionService.getRandomQuestionsByDifficulty(difficultyEnum, questionCount);
            } else {
                // No filters
                questions = questionService.getRandomQuestions(questionCount);
            }

            if (questions.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "No hay suficientes preguntas disponibles con los filtros seleccionados");
                return "redirect:/test";
            }

            model.addAttribute("questions", questions);
            model.addAttribute("questionCount", questions.size());
            model.addAttribute("themeId", themeId);
            model.addAttribute("difficulty", difficulty);
            return "test/test-execution";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al generar el test: " + e.getMessage());
            return "redirect:/test";
        }
    }

    @PostMapping("/submit")
    public String submitTest(
            @RequestParam MultiValueMap<String, String> allParams,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {

        System.out.println("\n=== TEST SUBMISSION STARTED ===");
        System.out.println("User: " + userDetails.getUsername());

        try {
            // Get current user
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            System.out.println("User found: " + user.getId());

            // Extract question IDs and answers
            List<Question> questions = new ArrayList<>();
            Map<Long, String> userAnswers = new HashMap<>();
            Set<Long> processedQuestions = new HashSet<>();

            for (Map.Entry<String, List<String>> entry : allParams.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("answer_")) {
                    Long questionId = Long.parseLong(key.substring(7));

                    // Avoid duplicates
                    if (!processedQuestions.contains(questionId)) {
                        List<String> values = entry.getValue();

                        // For multiple choice, join all values with comma
                        String answer = String.join(",", values);
                        userAnswers.put(questionId, answer);

                        // Load question
                        questionService.findById(questionId).ifPresent(questions::add);
                        processedQuestions.add(questionId);
                    }
                }
            }

            // Extract filters
            Long themeId = allParams.getFirst("themeId") != null && !allParams.getFirst("themeId").isEmpty()
                    ? Long.parseLong(allParams.getFirst("themeId"))
                    : null;

            Difficulty difficulty = null;
            String difficultyStr = allParams.getFirst("difficulty");
            if (difficultyStr != null && !difficultyStr.isEmpty()) {
                try {
                    difficulty = Difficulty.valueOf(difficultyStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // Ignore invalid difficulty
                }
            }

            // Evaluate and save test
            System.out.println("Questions: " + questions.size() + ", Answers: " + userAnswers.size());
            TestResult result = testService.evaluateAndSaveTest(user, questions, userAnswers, themeId, difficulty);
            System.out.println("Result saved! ID: " + result.getId() + ", Score: " + result.getScore() + "%");

            System.out.println("=== TEST SUBMISSION COMPLETE ===\n");

            // Redirect to results page
            return "redirect:/test/result/" + result.getId();

        } catch (Exception e) {
            System.err.println("ERROR in submitTest:");
            e.printStackTrace(); // For debugging
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al evaluar el test: " + e.getMessage());
            return "redirect:/test";
        }
    }

    @GetMapping("/result/{id}")
    public String showResult(
            @org.springframework.web.bind.annotation.PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            TestResult result = testService.getResultById(id);
            model.addAttribute("result", result);
            return "test/test-result";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Resultado no encontrado");
            return "redirect:/test";
        }
    }
}
