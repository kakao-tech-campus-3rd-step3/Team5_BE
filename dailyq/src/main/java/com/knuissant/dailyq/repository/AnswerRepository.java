package com.knuissant.dailyq.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.knuissant.dailyq.domain.answers.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long>,
        JpaSpecificationExecutor<Answer> {

    // JPA 쿼리 메서드를 사용하여 특정 사용자의 특정 날짜 범위 내 답변 개수를 조회
    long countByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}

