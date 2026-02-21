package com.miempresa.miprimertfg.service;

import com.miempresa.miprimertfg.model.*;
import com.miempresa.miprimertfg.repository.TestResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TestService {

    private final QuestionService questionService;
    private final TestResultRepository testResultRepository;
    private final UserService userService;

    /**
     * Generate a random test with specified parameters
     */
    public List<Question> generateTest(Long themeId, Difficulty difficulty, int questionCount) {
        if (themeId != null) {
            return questionService.getRandomQuestionsByTheme(themeId, questionCount);
        } else if (difficulty != null) {
            return questionService.getRandomQuestionsByDifficulty(difficulty, questionCount);
        } else {
            return questionService.getRandomQuestions(questionCount);
        }
    }

    /**
     * Evaluate test answers and calculate score
     */
    @Transactional
    public TestResult evaluateAndSaveTest(User user, List<Question> questions,
            Map<Long, String> userAnswers, Long themeId, Difficulty difficulty) {

        System.out.println("=== EVALUATING TEST ===");
        System.out.println("User: " + user.getUsername());
        System.out.println("Total questions: " + questions.size());
        System.out.println("User answers: " + userAnswers.size());

        int correctAnswers = 0;

        // Validate each answer
        for (Question question : questions) {
            String userAnswer = userAnswers.get(question.getId());
            boolean isCorrect = userAnswer != null && validateAnswer(question, userAnswer);
            if (isCorrect) {
                correctAnswers++;
            }
            System.out
                    .println("Q" + question.getId() + ": " + (isCorrect ? "✓" : "✗") + " (answer: " + userAnswer + ")");
        }

        System.out.println("Correct answers: " + correctAnswers + "/" + questions.size());

        // Create and save test result
        TestResult result = new TestResult();
        result.setUser(user);
        result.setTotalQuestions(questions.size());
        result.setCorrectAnswers(correctAnswers);
        // score is calculated automatically in @PrePersist

        if (themeId != null && !questions.isEmpty()) {
            Theme theme = questions.get(0).getTheme();
            result.setTheme(theme);
            System.out.println("Theme: " + (theme != null ? theme.getName() : "null"));
        }
        result.setDifficulty(difficulty);
        System.out.println("Difficulty: " + difficulty);

        TestResult savedResult = testResultRepository.save(result);
        System.out.println("Result saved with ID: " + savedResult.getId());
        System.out.println("Score: " + savedResult.getScore() + "%");
        System.out.println("=== TEST EVALUATION COMPLETE ===");

        return savedResult;
    }

    private boolean validateAnswer(Question question, String userAnswer) {
        if (question instanceof TrueFalseQuestion) {
            TrueFalseQuestion tfq = (TrueFalseQuestion) question;
            return String.valueOf(tfq.getCorrectAnswer()).equals(userAnswer);
        } else if (question instanceof SingleChoiceQuestion) {
            SingleChoiceQuestion scq = (SingleChoiceQuestion) question;
            try {
                int answerIndex = Integer.parseInt(userAnswer);
                return scq.getCorrectAnswerIndex() == answerIndex;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (question instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
            try {
                String[] answerParts = userAnswer.split(",");
                List<Integer> userIndices = new java.util.ArrayList<>();
                for (String part : answerParts) {
                    userIndices.add(Integer.parseInt(part.trim()));
                }
                userIndices.sort(Integer::compareTo);
                List<Integer> correctIndices = new java.util.ArrayList<>(mcq.getCorrectAnswerIndices());
                correctIndices.sort(Integer::compareTo);
                return userIndices.equals(correctIndices);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Get test results for a specific user
     */
    public Page<TestResult> getUserResults(User user, Pageable pageable) {
        return testResultRepository.findByUser(user, pageable);
    }

    /**
     * Get recent test results for a user
     */
    public List<TestResult> getRecentUserResults(User user) {
        return testResultRepository.findByUserOrderByCompletedAtDesc(user);
    }

    /**
     * Get test result by ID
     */
    public TestResult getResultById(Long id) {
        return testResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resultado no encontrado"));
    }

    /**
     * Count tests taken by user
     */
    public long countUserTests(User user) {
        return testResultRepository.countByUser(user);
    }

    /**
     * Get user statistics
     */
    public Map<String, Object> getUserStatistics(User user) {
        List<TestResult> results = testResultRepository.findByUserOrderByCompletedAtDesc(user);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTests", results.size());

        if (!results.isEmpty()) {
            double avgScore = results.stream()
                    .mapToDouble(TestResult::getScore)
                    .average()
                    .orElse(0.0);

            double maxScore = results.stream()
                    .mapToDouble(TestResult::getScore)
                    .max()
                    .orElse(0.0);

            stats.put("averageScore", avgScore);
            stats.put("bestScore", maxScore);
            stats.put("lastTestDate", results.get(0).getCompletedAt());
        } else {
            stats.put("averageScore", 0.0);
            stats.put("bestScore", 0.0);
            stats.put("lastTestDate", null);
        }

        return stats;
    }
}
