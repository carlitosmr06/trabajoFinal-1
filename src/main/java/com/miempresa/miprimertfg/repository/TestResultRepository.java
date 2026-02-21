package com.miempresa.miprimertfg.repository;

import com.miempresa.miprimertfg.model.TestResult;
import com.miempresa.miprimertfg.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    Page<TestResult> findByUser(User user, Pageable pageable);

    List<TestResult> findByUserOrderByCompletedAtDesc(User user);

    Page<TestResult> findByUserAndCompletedAtBetween(User user, LocalDateTime start, LocalDateTime end,
            Pageable pageable);

    long countByUser(User user);
}
