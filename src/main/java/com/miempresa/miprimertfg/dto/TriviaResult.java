package com.miempresa.miprimertfg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class TriviaResult {
    private String category;
    private String type;
    private String difficulty;
    private String question;

    @JsonProperty("correct_answer")
    private String correctAnswer;

    @JsonProperty("incorrect_answers")
    private List<String> incorrectAnswers;
}
