package com.miempresa.miprimertfg.repository;

import com.miempresa.miprimertfg.model.Difficulty;
import com.miempresa.miprimertfg.model.Question;
import com.miempresa.miprimertfg.model.Theme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findByTheme(Theme theme, Pageable pageable);

    Page<Question> findByDifficulty(Difficulty difficulty, Pageable pageable);

    Page<Question> findByThemeAndDifficulty(Theme theme, Difficulty difficulty, Pageable pageable);

    long countByTheme(Theme theme);

    long countByDifficulty(Difficulty difficulty);

    @Query("SELECT q FROM Question q WHERE TYPE(q) = :questionType")
    Page<Question> findByQuestionType(@Param("questionType") Class<? extends Question> questionType, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.theme = :theme AND TYPE(q) = :questionType")
    Page<Question> findByThemeAndQuestionType(@Param("theme") Theme theme,
            @Param("questionType") Class<? extends Question> questionType,
            Pageable pageable);

    @Query("SELECT q FROM Question q ORDER BY FUNCTION('RAND')")
    List<Question> findRandomQuestions(Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.theme = :theme ORDER BY FUNCTION('RAND')")
    List<Question> findRandomQuestionsByTheme(@Param("theme") Theme theme, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.difficulty = :difficulty ORDER BY FUNCTION('RAND')")
    List<Question> findRandomQuestionsByDifficulty(@Param("difficulty") Difficulty difficulty, Pageable pageable);
}
