package com.miempresa.miprimertfg.repository;

import com.miempresa.miprimertfg.model.SingleChoiceQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SingleChoiceQuestionRepository extends JpaRepository<SingleChoiceQuestion, Long> {
}
