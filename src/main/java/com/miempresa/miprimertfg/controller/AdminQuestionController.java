package com.miempresa.miprimertfg.controller;

import com.miempresa.miprimertfg.model.*;
import com.miempresa.miprimertfg.service.QuestionService;
import com.miempresa.miprimertfg.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/questions")
@RequiredArgsConstructor
public class AdminQuestionController {

    private final QuestionService questionService;
    private final ThemeService themeService;

    @GetMapping
    public String listQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
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

        return "admin/questions";
    }

    @GetMapping("/new")
    public String newQuestionForm(Model model) {
        List<Theme> themes = themeService.findAll();
        model.addAttribute("themes", themes);
        model.addAttribute("difficulties", Difficulty.values());
        model.addAttribute("questionTypes", Arrays.asList("TRUE_FALSE", "SINGLE_CHOICE", "MULTIPLE_CHOICE"));
        return "admin/question-form";
    }

    @PostMapping
    public String createQuestion(
            @RequestParam String questionType,
            @RequestParam String questionText,
            @RequestParam Long themeId,
            @RequestParam Difficulty difficulty,
            @RequestParam(required = false) Boolean trueFalseAnswer,
            @RequestParam(required = false) List<String> options,
            @RequestParam(required = false) Integer singleChoiceAnswer,
            @RequestParam(required = false) List<Integer> multipleChoiceAnswers,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Theme theme = themeService.findById(themeId)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado"));

        String username = authentication.getName();

        try {
            switch (questionType.toUpperCase()) {
                case "TRUE_FALSE":
                    TrueFalseQuestion tfq = new TrueFalseQuestion();
                    tfq.setQuestionText(questionText);
                    tfq.setTheme(theme);
                    tfq.setDifficulty(difficulty);
                    tfq.setCreatedBy(username);
                    tfq.setCorrectAnswer(trueFalseAnswer != null && trueFalseAnswer);
                    questionService.saveTrueFalse(tfq);
                    break;

                case "SINGLE_CHOICE":
                    SingleChoiceQuestion scq = new SingleChoiceQuestion();
                    scq.setQuestionText(questionText);
                    scq.setTheme(theme);
                    scq.setDifficulty(difficulty);
                    scq.setCreatedBy(username);
                    scq.setOptions(options);
                    scq.setCorrectAnswerIndex(singleChoiceAnswer != null ? singleChoiceAnswer : 0);
                    questionService.saveSingleChoice(scq);
                    break;

                case "MULTIPLE_CHOICE":
                    MultipleChoiceQuestion mcq = new MultipleChoiceQuestion();
                    mcq.setQuestionText(questionText);
                    mcq.setTheme(theme);
                    mcq.setDifficulty(difficulty);
                    mcq.setCreatedBy(username);
                    mcq.setOptions(options);
                    mcq.setCorrectAnswerIndices(
                            multipleChoiceAnswers != null ? multipleChoiceAnswers : Arrays.asList(0));
                    questionService.saveMultipleChoice(mcq);
                    break;

                default:
                    throw new IllegalArgumentException("Tipo de pregunta no válido");
            }

            redirectAttributes.addFlashAttribute("successMessage", "Pregunta creada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear la pregunta: " + e.getMessage());
        }

        return "redirect:/admin/questions";
    }

    @PostMapping("/{id}/delete")
    public String deleteQuestion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            questionService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Pregunta eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la pregunta: " + e.getMessage());
        }
        return "redirect:/admin/questions";
    }

    @GetMapping("/{id}/edit")
    public String editQuestionForm(@PathVariable Long id, Model model) {
        Question question = questionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Pregunta no encontrada"));

        List<Theme> themes = themeService.findAll();
        model.addAttribute("question", question);
        model.addAttribute("themes", themes);
        model.addAttribute("difficulties", Difficulty.values());
        model.addAttribute("questionTypes", Arrays.asList("TRUE_FALSE", "SINGLE_CHOICE", "MULTIPLE_CHOICE"));
        return "admin/question-form";
    }

    @PostMapping("/{id}/edit")
    public String updateQuestion(
            @PathVariable Long id,
            @RequestParam String questionText,
            @RequestParam Long themeId,
            @RequestParam Difficulty difficulty,
            @RequestParam(required = false) Boolean trueFalseAnswer,
            @RequestParam(required = false) List<String> options,
            @RequestParam(required = false) Integer singleChoiceAnswer,
            @RequestParam(required = false) List<Integer> multipleChoiceAnswers,
            RedirectAttributes redirectAttributes) {

        try {
            Question question = questionService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pregunta no encontrada"));
            Theme theme = themeService.findById(themeId)
                    .orElseThrow(() -> new RuntimeException("Tema no encontrado"));

            question.setQuestionText(questionText);
            question.setTheme(theme);
            question.setDifficulty(difficulty);

            if (question instanceof TrueFalseQuestion) {
                ((TrueFalseQuestion) question).setCorrectAnswer(trueFalseAnswer != null && trueFalseAnswer);
                questionService.saveTrueFalse((TrueFalseQuestion) question);
            } else if (question instanceof SingleChoiceQuestion) {
                ((SingleChoiceQuestion) question).setOptions(options);
                ((SingleChoiceQuestion) question)
                        .setCorrectAnswerIndex(singleChoiceAnswer != null ? singleChoiceAnswer : 0);
                questionService.saveSingleChoice((SingleChoiceQuestion) question);
            } else if (question instanceof MultipleChoiceQuestion) {
                ((MultipleChoiceQuestion) question).setOptions(options);
                ((MultipleChoiceQuestion) question).setCorrectAnswerIndices(
                        multipleChoiceAnswers != null ? multipleChoiceAnswers : Arrays.asList(0));
                questionService.saveMultipleChoice((MultipleChoiceQuestion) question);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Pregunta actualizada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar la pregunta: " + e.getMessage());
        }

        return "redirect:/admin/questions";
    }
}
