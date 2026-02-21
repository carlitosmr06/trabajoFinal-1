package com.miempresa.miprimertfg.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "questionType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TrueFalseQuestion.class, name = "TRUE_FALSE"),
        @JsonSubTypes.Type(value = SingleChoiceQuestion.class, name = "SINGLE_CHOICE"),
        @JsonSubTypes.Type(value = MultipleChoiceQuestion.class, name = "MULTIPLE_CHOICE")
})
public abstract class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El texto de la pregunta es obligatorio")
    @Column(nullable = false, length = 1000)
    private String questionText;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        if (createdBy == null) {
            createdBy = "system";
        }
    }

    // Abstract method to be implemented by subclasses
    @com.fasterxml.jackson.annotation.JsonProperty("questionType")
    public abstract String getQuestionType();

    // Abstract method to validate an answer
    public abstract boolean validateAnswer(Object answer);
}
