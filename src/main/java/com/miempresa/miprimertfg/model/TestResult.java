package com.miempresa.miprimertfg.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_results")
@Data
@NoArgsConstructor
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer totalQuestions;

    @Column(nullable = false)
    private Integer correctAnswers;

    @Column(nullable = false)
    private Double score; // Percentage 0-100

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Difficulty difficulty;

    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    @PrePersist
    protected void onCreate() {
        completedAt = LocalDateTime.now();
        if (totalQuestions != null && totalQuestions > 0) {
            score = (correctAnswers.doubleValue() / totalQuestions.doubleValue()) * 100.0;
        }
    }

    public String getScoreFormatted() {
        return String.format("%.1f%%", score);
    }

    public String getResultText() {
        return correctAnswers + "/" + totalQuestions;
    }
}
