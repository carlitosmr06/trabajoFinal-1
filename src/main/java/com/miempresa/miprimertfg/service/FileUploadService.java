package com.miempresa.miprimertfg.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.miempresa.miprimertfg.model.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FileUploadService {

    private final QuestionService questionService;
    private final ThemeService themeService;
    private final Gson gson = new Gson();

    /**
     * Upload questions from file (CSV or JSON)
     */
    public List<Question> uploadQuestions(MultipartFile file, String createdBy) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IOException("Nombre de archivo inválido");
        }

        if (filename.endsWith(".csv")) {
            return uploadFromCSV(file, createdBy);
        } else if (filename.endsWith(".json")) {
            return uploadFromJSON(file, createdBy);
        } else {
            throw new IOException("Formato de archivo no soportado. Use CSV o JSON");
        }
    }

    /**
     * Upload from CSV format:
     * type,question,theme,difficulty,answer,[options...]
     * 
     * Examples:
     * TRUE_FALSE,"¿Java es un lenguaje orientado a objetos?",Java,EASY,true
     * SINGLE_CHOICE,"¿Qué es Spring Boot?",Spring,MEDIUM,0,"Framework
     * Java","Lenguaje","Base de datos","IDE"
     * MULTIPLE_CHOICE,"Selecciona los tipos
     * primitivos",Java,HARD,"0,2,3","int","String","boolean","double"
     */
    private List<Question> uploadFromCSV(MultipartFile file, String createdBy) throws IOException {
        List<Question> questions = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> records = reader.readAll();

            // Skip header if exists
            boolean hasHeader = records.get(0)[0].equalsIgnoreCase("type");
            int startIndex = hasHeader ? 1 : 0;

            for (int i = startIndex; i < records.size(); i++) {
                String[] record = records.get(i);

                try {
                    Question question = parseCSVRecord(record, createdBy);
                    questions.add(questionService.save(question));
                } catch (Exception e) {
                    System.err.println("Error en línea " + (i + 1) + ": " + e.getMessage());
                }
            }

        } catch (CsvException e) {
            throw new IOException("Error al procesar archivo CSV: " + e.getMessage(), e);
        }

        return questions;
    }

    private Question parseCSVRecord(String[] record, String createdBy) {
        String type = record[0].trim().toUpperCase();
        String questionText = record[1].trim();
        String themeName = record[2].trim();
        Difficulty difficulty = Difficulty.valueOf(record[3].trim().toUpperCase());

        Theme theme = themeService.findOrCreate(themeName);

        Question question;

        switch (type) {
            case "TRUE_FALSE" -> {
                TrueFalseQuestion tfq = new TrueFalseQuestion();
                tfq.setQuestionText(questionText);
                tfq.setTheme(theme);
                tfq.setDifficulty(difficulty);
                tfq.setCreatedBy(createdBy);
                tfq.setCorrectAnswer(Boolean.parseBoolean(record[4].trim()));
                question = tfq;
            }
            case "SINGLE_CHOICE" -> {
                SingleChoiceQuestion scq = new SingleChoiceQuestion();
                scq.setQuestionText(questionText);
                scq.setTheme(theme);
                scq.setDifficulty(difficulty);
                scq.setCreatedBy(createdBy);
                scq.setCorrectAnswerIndex(Integer.parseInt(record[4].trim()));

                List<String> options = new ArrayList<>();
                for (int i = 5; i < record.length; i++) {
                    options.add(record[i].trim());
                }
                scq.setOptions(options);
                question = scq;
            }
            case "MULTIPLE_CHOICE" -> {
                MultipleChoiceQuestion mcq = new MultipleChoiceQuestion();
                mcq.setQuestionText(questionText);
                mcq.setTheme(theme);
                mcq.setDifficulty(difficulty);
                mcq.setCreatedBy(createdBy);

                // Parse correct answers (e.g., "0,2,3")
                String[] correctAnswers = record[4].trim().split(",");
                List<Integer> correctIndices = new ArrayList<>();
                for (String answer : correctAnswers) {
                    correctIndices.add(Integer.parseInt(answer.trim()));
                }
                mcq.setCorrectAnswerIndices(correctIndices);

                List<String> options = new ArrayList<>();
                for (int i = 5; i < record.length; i++) {
                    options.add(record[i].trim());
                }
                mcq.setOptions(options);
                question = mcq;
            }
            default -> throw new IllegalArgumentException("Tipo de pregunta desconocido: " + type);
        }

        return question;
    }

    /**
     * Upload from JSON format:
     * [
     * {
     * "type": "TRUE_FALSE",
     * "question": "...",
     * "theme": "...",
     * "difficulty": "EASY",
     * "correctAnswer": true
     * },
     * ...
     * ]
     */
    private List<Question> uploadFromJSON(MultipartFile file, String createdBy) throws IOException {
        List<Question> questions = new ArrayList<>();

        String content = new String(file.getBytes());
        JsonArray jsonArray = gson.fromJson(content, JsonArray.class);

        for (JsonElement element : jsonArray) {
            JsonObject obj = element.getAsJsonObject();

            try {
                Question question = parseJSONObject(obj, createdBy);
                questions.add(questionService.save(question));
            } catch (Exception e) {
                System.err.println("Error al procesar pregunta: " + e.getMessage());
            }
        }

        return questions;
    }

    private Question parseJSONObject(JsonObject obj, String createdBy) {
        String type = obj.get("type").getAsString().toUpperCase();
        String questionText = obj.get("question").getAsString();
        String themeName = obj.get("theme").getAsString();
        Difficulty difficulty = Difficulty.valueOf(obj.get("difficulty").getAsString().toUpperCase());

        Theme theme = themeService.findOrCreate(themeName);

        Question question;

        switch (type) {
            case "TRUE_FALSE" -> {
                TrueFalseQuestion tfq = new TrueFalseQuestion();
                tfq.setQuestionText(questionText);
                tfq.setTheme(theme);
                tfq.setDifficulty(difficulty);
                tfq.setCreatedBy(createdBy);
                tfq.setCorrectAnswer(obj.get("correctAnswer").getAsBoolean());
                question = tfq;
            }
            case "SINGLE_CHOICE" -> {
                SingleChoiceQuestion scq = new SingleChoiceQuestion();
                scq.setQuestionText(questionText);
                scq.setTheme(theme);
                scq.setDifficulty(difficulty);
                scq.setCreatedBy(createdBy);
                scq.setCorrectAnswerIndex(obj.get("correctAnswerIndex").getAsInt());

                JsonArray optionsArray = obj.getAsJsonArray("options");
                List<String> options = new ArrayList<>();
                for (JsonElement optEl : optionsArray) {
                    options.add(optEl.getAsString());
                }
                scq.setOptions(options);
                question = scq;
            }
            case "MULTIPLE_CHOICE" -> {
                MultipleChoiceQuestion mcq = new MultipleChoiceQuestion();
                mcq.setQuestionText(questionText);
                mcq.setTheme(theme);
                mcq.setDifficulty(difficulty);
                mcq.setCreatedBy(createdBy);

                JsonArray correctArray = obj.getAsJsonArray("correctAnswerIndices");
                List<Integer> correctIndices = new ArrayList<>();
                for (JsonElement corrEl : correctArray) {
                    correctIndices.add(corrEl.getAsInt());
                }
                mcq.setCorrectAnswerIndices(correctIndices);

                JsonArray optionsArray = obj.getAsJsonArray("options");
                List<String> options = new ArrayList<>();
                for (JsonElement optEl : optionsArray) {
                    options.add(optEl.getAsString());
                }
                mcq.setOptions(options);
                question = mcq;
            }
            default -> throw new IllegalArgumentException("Tipo de pregunta desconocido: " + type);
        }

        return question;
    }
}
