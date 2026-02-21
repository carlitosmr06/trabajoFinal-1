package com.miempresa.miprimertfg.service;

import com.miempresa.miprimertfg.model.*;
import com.miempresa.miprimertfg.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final TrueFalseQuestionRepository trueFalseQuestionRepository;
    private final SingleChoiceQuestionRepository singleChoiceQuestionRepository;
    private final MultipleChoiceQuestionRepository multipleChoiceQuestionRepository;
    private final ThemeRepository themeRepository;

    // ========== CRUD Operations ==========

    public Question save(Question question) {
        return questionRepository.save(question);
    }

    public TrueFalseQuestion saveTrueFalse(TrueFalseQuestion question) {
        return trueFalseQuestionRepository.save(question);
    }

    public SingleChoiceQuestion saveSingleChoice(SingleChoiceQuestion question) {
        return singleChoiceQuestionRepository.save(question);
    }

    public MultipleChoiceQuestion saveMultipleChoice(MultipleChoiceQuestion question) {
        return multipleChoiceQuestionRepository.save(question);
    }

    public Optional<Question> findById(Long id) {
        return questionRepository.findById(id);
    }

    public List<Question> findAllById(List<Long> ids) {
        return questionRepository.findAllById(ids);
    }

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public Page<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    public void deleteById(Long id) {
        questionRepository.deleteById(id);
    }

    // ========== Filtering ==========

    public Page<Question> findByTheme(Long themeId, Pageable pageable) {
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado"));
        return questionRepository.findByTheme(theme, pageable);
    }

    public Page<Question> findByDifficulty(Difficulty difficulty, Pageable pageable) {
        return questionRepository.findByDifficulty(difficulty, pageable);
    }

    public Page<Question> findByThemeAndDifficulty(Long themeId, Difficulty difficulty, Pageable pageable) {
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado"));
        return questionRepository.findByThemeAndDifficulty(theme, difficulty, pageable);
    }

    public Page<Question> findByQuestionType(String questionType, Pageable pageable) {
        Class<? extends Question> clazz = getQuestionClass(questionType);
        return questionRepository.findByQuestionType(clazz, pageable);
    }

    public Page<Question> findByFilters(Long themeId, Difficulty difficulty, String questionType, Pageable pageable) {
        if (themeId != null && difficulty != null && questionType != null) {
            Theme theme = themeRepository.findById(themeId).orElseThrow();
            Class<? extends Question> clazz = getQuestionClass(questionType);
            return questionRepository.findByThemeAndQuestionType(theme, clazz, pageable);
        } else if (themeId != null && difficulty != null) {
            return findByThemeAndDifficulty(themeId, difficulty, pageable);
        } else if (themeId != null) {
            return findByTheme(themeId, pageable);
        } else if (difficulty != null) {
            return findByDifficulty(difficulty, pageable);
        } else if (questionType != null) {
            return findByQuestionType(questionType, pageable);
        } else {
            return findAll(pageable);
        }
    }

    // ========== Random Question Generation ==========

    public List<Question> getRandomQuestions(int count) {
        Pageable pageable = PageRequest.of(0, count);
        return questionRepository.findRandomQuestions(pageable);
    }

    public List<Question> getRandomQuestionsByTheme(Long themeId, int count) {
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado"));
        Pageable pageable = PageRequest.of(0, count);
        return questionRepository.findRandomQuestionsByTheme(theme, pageable);
    }

    public List<Question> getRandomQuestionsByDifficulty(Difficulty difficulty, int count) {
        Pageable pageable = PageRequest.of(0, count);
        return questionRepository.findRandomQuestionsByDifficulty(difficulty, pageable);
    }

    // ========== Statistics ==========

    public long countAll() {
        return questionRepository.count();
    }

    public long countByTheme(Long themeId) {
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado"));
        return questionRepository.countByTheme(theme);
    }

    public long countByDifficulty(Difficulty difficulty) {
        return questionRepository.countByDifficulty(difficulty);
    }

    // ========== Helper Methods ==========

    private Class<? extends Question> getQuestionClass(String questionType) {
        return switch (questionType.toUpperCase()) {
            case "TRUE_FALSE" -> TrueFalseQuestion.class;
            case "SINGLE_CHOICE" -> SingleChoiceQuestion.class;
            case "MULTIPLE_CHOICE" -> MultipleChoiceQuestion.class;
            default -> throw new IllegalArgumentException("Tipo de pregunta no válido: " + questionType);
        };
    }
}
