package com.miempresa.miprimertfg.controller.api;

import com.miempresa.miprimertfg.model.*;
import com.miempresa.miprimertfg.service.QuestionService;
import com.miempresa.miprimertfg.service.TestService;
import com.miempresa.miprimertfg.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Tests", description = "API de tests y evaluaciones")
@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestRestController {

    private final TestService testService;
    private final QuestionService questionService;
    private final UserService userService;

    @Data
    public static class GenerateTestRequest {
        private Long themeId;
        private String difficulty;
        private int questionCount = 10;
    }

    @Data
    public static class SubmitTestRequest {
        private List<Long> questionIds;
        // Answers: key = questionId (as String), value = answer (String or array)
        private Map<String, Object> answers;
    }

    @Operation(summary = "Generar test", description = "Generar un nuevo test con parámetros específicos")
    @PostMapping("/generate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> generateTest(@RequestBody GenerateTestRequest request) {
        try {
            Difficulty difficulty = null;
            if (request.getDifficulty() != null && !request.getDifficulty().isBlank()) {
                difficulty = Difficulty.valueOf(request.getDifficulty().toUpperCase());
            }
            List<Question> questions = testService.generateTest(
                    request.getThemeId(),
                    difficulty,
                    request.getQuestionCount());
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "Enviar test", description = "Enviar respuestas y obtener calificación")
    @PostMapping("/submit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> submitTest(
            @RequestBody SubmitTestRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Fetch the actual question objects
            List<Question> questions = questionService.findAllById(request.getQuestionIds());

            // Convert answers map: keys may come as String from JSON, convert to
            // Long->String
            Map<Long, String> answersConverted = new HashMap<>();
            if (request.getAnswers() != null) {
                for (Map.Entry<String, Object> entry : request.getAnswers().entrySet()) {
                    Long qId = Long.parseLong(entry.getKey());
                    Object val = entry.getValue();
                    String answerStr;
                    if (val instanceof List<?> list) {
                        // Multiple choice: list of indices -> "0,2,3"
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < list.size(); i++) {
                            if (i > 0)
                                sb.append(",");
                            sb.append(list.get(i).toString().replace(".0", ""));
                        }
                        answerStr = sb.toString();
                    } else {
                        answerStr = val.toString().replace(".0", "");
                    }
                    answersConverted.put(qId, answerStr);
                }
            }

            // Determine theme and difficulty from the questions themselves
            Long themeId = questions.isEmpty() || questions.get(0).getTheme() == null
                    ? null
                    : questions.get(0).getTheme().getId();
            Difficulty difficulty = questions.isEmpty() ? null : questions.get(0).getDifficulty();

            TestResult result = testService.evaluateAndSaveTest(
                    user,
                    questions,
                    answersConverted,
                    themeId,
                    difficulty);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "Obtener resultados", description = "Obtener historial de resultados del usuario")
    @GetMapping("/results")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Pageable pageable = PageRequest.of(page, size);
            Page<TestResult> results = testService.getUserResults(user, pageable);

            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "Obtener estadísticas", description = "Obtener estadísticas del usuario")
    @GetMapping("/statistics")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserStatistics(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Map<String, Object> stats = testService.getUserStatistics(user);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
