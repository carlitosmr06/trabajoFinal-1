package com.miempresa.miprimertfg.service;

import com.miempresa.miprimertfg.dto.TriviaResponse;
import com.miempresa.miprimertfg.dto.TriviaResult;
import com.miempresa.miprimertfg.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TriviaApiService {

    private final WebClient.Builder webClientBuilder;
    private final QuestionService questionService;
    private final ThemeService themeService;

    // Base URL for Open Trivia DB
    private static final String API_URL = "https://opentdb.com/api.php";

    public int importQuestions(int amount, Long themeId, String difficulty) {
        Theme theme = themeService.findById(themeId)
                .orElseThrow(() -> new RuntimeException("Theme not found"));

        // Build URL parameters
        String url = API_URL + "?amount=" + amount;
        if (difficulty != null && !difficulty.isEmpty()) {
            url += "&difficulty=" + difficulty.toLowerCase();
        }

        // Fetch data from API
        TriviaResponse response = webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(TriviaResponse.class)
                .block();

        if (response == null || response.getResults() == null) {
            return 0;
        }

        int importedCount = 0;
        for (TriviaResult result : response.getResults()) {
            try {
                processAndSaveQuestion(result, theme);
                importedCount++;
            } catch (Exception e) {
                System.err.println("Error importing question: " + e.getMessage());
            }
        }
        return importedCount;
    }

    private void processAndSaveQuestion(TriviaResult result, Theme theme) {
        // Decode HTML entities in text
        String questionText = HtmlUtils.htmlUnescape(result.getQuestion());
        String correctAnswer = HtmlUtils.htmlUnescape(result.getCorrectAnswer());
        List<String> incorrectAnswers = result.getIncorrectAnswers().stream()
                .map(HtmlUtils::htmlUnescape)
                .toList();

        Difficulty diff = Difficulty.MEDIUM;
        try {
            diff = Difficulty.valueOf(result.getDifficulty().toUpperCase());
        } catch (Exception e) {
            // Default to MEDIUM if parsing fails
        }

        if ("boolean".equals(result.getType())) {
            // True/False Question
            TrueFalseQuestion scaleq = new TrueFalseQuestion();
            scaleq.setQuestionText(questionText);
            scaleq.setTheme(theme);
            scaleq.setDifficulty(diff);
            scaleq.setCreatedBy("api_import");
            scaleq.setCorrectAnswer(Boolean.parseBoolean(correctAnswer));
            questionService.saveTrueFalse(scaleq);
        } else {
            // Multiple Choice (mapped to Single Choice in our system for simplicity of API
            // structure)
            // Open Trivia DB 'multiple' type is actually single correct answer among
            // distractors
            SingleChoiceQuestion scq = new SingleChoiceQuestion();
            scq.setQuestionText(questionText);
            scq.setTheme(theme);
            scq.setDifficulty(diff);
            scq.setCreatedBy("api_import");

            // Combine correct and incorrect answers and shuffle
            List<String> options = new ArrayList<>(incorrectAnswers);
            options.add(correctAnswer);
            // Simple shuffle/randomization would be good here,
            // but for simplicity we just add it at the end and set index
            // Ideally we should shuffle and find the new index of correct answer

            java.util.Collections.shuffle(options);
            int correctIndex = options.indexOf(correctAnswer);

            scq.setOptions(options);
            scq.setCorrectAnswerIndex(correctIndex);
            questionService.saveSingleChoice(scq);
        }
    }
}
