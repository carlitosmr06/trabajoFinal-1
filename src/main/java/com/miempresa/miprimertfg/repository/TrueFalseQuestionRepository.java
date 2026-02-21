package com.miempresa.miprimertfg.repository;

import com.miempresa.miprimertfg.model.TrueFalseQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrueFalseQuestionRepository extends JpaRepository<TrueFalseQuestion, Long> {
}
