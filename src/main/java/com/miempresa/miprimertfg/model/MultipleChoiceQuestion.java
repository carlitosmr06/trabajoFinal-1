package com.miempresa.miprimertfg.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "multiple_choice_questions")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MultipleChoiceQuestion extends Question {

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "multiple_choice_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_text", length = 500)
    @OrderColumn(name = "option_order")
    @NotEmpty(message = "Debe haber al menos una opción")
    private List<String> options = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "multiple_choice_correct_answers", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "correct_index")
    @NotEmpty(message = "Debe haber al menos una respuesta correcta")
    private List<Integer> correctAnswerIndices = new ArrayList<>();

    @Override
    public String getQuestionType() {
        return "MULTIPLE_CHOICE";
    }

    @Override
    public boolean validateAnswer(Object answer) {
        if (answer instanceof List<?>) {
            List<?> answerList = (List<?>) answer;
            Set<Integer> answerSet = new HashSet<>();

            for (Object item : answerList) {
                if (item instanceof Integer) {
                    answerSet.add((Integer) item);
                } else if (item instanceof String) {
                    try {
                        answerSet.add(Integer.parseInt((String) item));
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
            }

            Set<Integer> correctSet = new HashSet<>(correctAnswerIndices);
            return answerSet.equals(correctSet);
        }
        return false;
    }
}
