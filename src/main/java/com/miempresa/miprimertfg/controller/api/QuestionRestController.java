package com.miempresa.miprimertfg.controller.api;

import com.miempresa.miprimertfg.model.*;
import com.miempresa.miprimertfg.service.QuestionService;
import com.miempresa.miprimertfg.service.ThemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "Questions", description = "API de gestión de preguntas")
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QuestionRestController {

    private final QuestionService questionService;
    private final ThemeService themeService;

    @Operation(summary = "Listar preguntas", description = "Obtener lista paginada de preguntas con filtros opcionales")
    @GetMapping
    public ResponseEntity<Page<Question>> getAllQuestions(
            @RequestParam(required = false) Long themeId,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String questionType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Difficulty diff = difficulty != null ? Difficulty.valueOf(difficulty.toUpperCase()) : null;

        Page<Question> questions = questionService.findByFilters(themeId, diff, questionType, pageable);
        return ResponseEntity.ok(questions);
    }

    @Operation(summary = "Obtener pregunta por ID", description = "Obtener una pregunta específica")
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long id) {
        Optional<Question> question = questionService.findById(id);
        if (question.isPresent()) {
            return ResponseEntity.ok(question.get());
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear pregunta", description = "Crear una nueva pregunta (requiere autenticación)")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createQuestion(@Valid @RequestBody Question question) {
        try {
            Question savedQuestion = questionService.save(question);
            return ResponseEntity.ok(savedQuestion);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar pregunta", description = "Actualizar una pregunta existente (requiere autenticación)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateQuestion(@PathVariable Long id, @Valid @RequestBody Question question) {
        try {
            Optional<Question> existing = questionService.findById(id);
            if (existing.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            question.setId(id);
            Question updated = questionService.save(question);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar pregunta", description = "Eliminar una pregunta (requiere rol ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        try {
            questionService.deleteById(id);
            return ResponseEntity.ok("Pregunta eliminada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Obtener preguntas aleatorias", description = "Generar conjunto de preguntas aleatorias")
    @GetMapping("/random")
    public ResponseEntity<?> getRandomQuestions(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) Long themeId,
            @RequestParam(required = false) String difficulty) {

        try {
            if (themeId != null) {
                return ResponseEntity.ok(questionService.getRandomQuestionsByTheme(themeId, count));
            } else if (difficulty != null) {
                Difficulty diff = Difficulty.valueOf(difficulty.toUpperCase());
                return ResponseEntity.ok(questionService.getRandomQuestionsByDifficulty(diff, count));
            } else {
                return ResponseEntity.ok(questionService.getRandomQuestions(count));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
