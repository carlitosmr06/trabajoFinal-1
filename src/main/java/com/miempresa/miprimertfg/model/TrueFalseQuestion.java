package com.miempresa.miprimertfg.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "true_false_questions")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TrueFalseQuestion extends Question {

    @Column(nullable = false)
    private Boolean correctAnswer;

    @Override
    public String getQuestionType() {
        return "TRUE_FALSE";
    }

    @Override
    public boolean validateAnswer(Object answer) {
        if (answer instanceof Boolean) {
            return correctAnswer.equals(answer);
        }
        if (answer instanceof String) {
            return correctAnswer.equals(Boolean.parseBoolean((String) answer));
        }
        return false;
    }
}
