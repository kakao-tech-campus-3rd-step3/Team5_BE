package com.knuissant.dailyq.domain.answers.repository;

import com.knuissant.dailyq.domain.answers.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AnswerRepository extends JpaRepository<Answer, Long>,
    JpaSpecificationExecutor<Answer> {
  }
