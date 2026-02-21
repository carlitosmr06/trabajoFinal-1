package com.miempresa.miprimertfg.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.miempresa.miprimertfg.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExternalApiService {

    private final QuestionService questionService;
    private final ThemeService themeService;
    private final WebClient.Builder webClientBuilder;

    private static final String TRIVIA_API_URL = "https://opentdb.com/api.php";

    /**
     * Import questions from Open Trivia Database API
     * 
     * @param amount     Number of questions to import
     * @param category   Category ID (optional)
     * @param difficulty Difficulty level (optional)
     * @return List of imported questions
     */
    public List<Question> importQuestionsFromTrivia(int amount, String category, String difficulty) {
        WebClient webClient = webClientBuilder.baseUrl(TRIVIA_API_URL).build();

        StringBuilder url = new StringBuilder("?amount=" + amount);
        if (category != null && !category.isEmpty()) {
            url.append("&category=").append(category);
        }
        if (difficulty != null && !difficulty.isEmpty()) {
            url.append("&difficulty=").append(difficulty.toLowerCase());
        }
        url.append("&type=multiple"); // Get multiple choice questions

        String response = webClient.get()
                .uri(url.toString())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (response == null) {
            throw new RuntimeException("No se pudo obtener respuesta de la API");
        }

        return parseTriviaApiResponse(response);
    }

    private List<Question> parseTriviaApiResponse(String jsonResponse) {
        List<Question> questions = new ArrayList<>();

        JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
        int responseCode = root.get("response_code").getAsInt();

        if (responseCode != 0) {
            throw new RuntimeException("Error en API de Trivia. Código: " + responseCode);
        }

        JsonArray results = root.getAsJsonArray("results");

        for (JsonElement element : results) {
            JsonObject qObj = element.getAsJsonObject();

            try {
                Question question = parseTriviaQuestion(qObj);
                questions.add(questionService.save(question));
            } catch (Exception e) {
                System.err.println("Error al procesar pregunta de trivia: " + e.getMessage());
            }
        }

        return questions;
    }

    private Question parseTriviaQuestion(JsonObject qObj) {
        String categoryName = qObj.get("category").getAsString();
        String questionText = decodeHtml(qObj.get("question").getAsString());
        String difficultyStr = qObj.get("difficulty").getAsString();
        String correctAnswer = decodeHtml(qObj.get("correct_answer").getAsString());

        // Get incorrect answers
        JsonArray incorrectAnswers = qObj.getAsJsonArray("incorrect_answers");
        List<String> options = new ArrayList<>();
        for (JsonElement el : incorrectAnswers) {
            options.add(decodeHtml(el.getAsString()));
        }

        // Insert correct answer at random position
        int correctIndex = (int) (Math.random() * (options.size() + 1));
        options.add(correctIndex, correctAnswer);

        // Map difficulty
        Difficulty difficulty = switch (difficultyStr.toLowerCase()) {
            case "easy" -> Difficulty.EASY;
            case "hard" -> Difficulty.HARD;
            default -> Difficulty.MEDIUM;
        };

        // Find or create theme
        Theme theme = themeService.findOrCreate(categoryName);

        // Create single choice question
        SingleChoiceQuestion question = new SingleChoiceQuestion();
        question.setQuestionText(questionText);
        question.setTheme(theme);
        question.setDifficulty(difficulty);
        question.setCreatedBy("OpenTriviaDB");
        question.setOptions(options);
        question.setCorrectAnswerIndex(correctIndex);

        return question;
    }

    /**
     * Decode HTML entities
     */
    private String decodeHtml(String text) {
        return text.replace("&quot;", "\"")
                .replace("&#039;", "'")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&eacute;", "é")
                .replace("&aacute;", "á")
                .replace("&iacute;", "í")
                .replace("&oacute;", "ó")
                .replace("&uacute;", "ú")
                .replace("&ntilde;", "ñ");
    }
}
