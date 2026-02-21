package com.miempresa.miprimertfg.repository;

import com.miempresa.miprimertfg.model.MultipleChoiceQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MultipleChoiceQuestionRepository extends JpaRepository<MultipleChoiceQuestion, Long> {
}
