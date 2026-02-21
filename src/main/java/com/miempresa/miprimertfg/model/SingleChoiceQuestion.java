package com.miempresa.miprimertfg.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "single_choice_questions")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SingleChoiceQuestion extends Question {

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "single_choice_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_text", length = 500)
    @OrderColumn(name = "option_order")
    @NotEmpty(message = "Debe haber al menos una opción")
    private List<String> options = new ArrayList<>();

    @NotNull(message = "Debe indicar la respuesta correcta")
    @Column(nullable = false)
    private Integer correctAnswerIndex;

    @Override
    public String getQuestionType() {
        return "SINGLE_CHOICE";
    }

    @Override
    public boolean validateAnswer(Object answer) {
        if (answer instanceof Integer) {
            return correctAnswerIndex.equals(answer);
        }
        if (answer instanceof String) {
            try {
                int answerIndex = Integer.parseInt((String) answer);
                return correctAnswerIndex.equals(answerIndex);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }
}
