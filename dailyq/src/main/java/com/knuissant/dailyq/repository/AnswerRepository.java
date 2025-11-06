package com.knuissant.dailyq.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.questions.Question;

public interface AnswerRepository extends JpaRepository<Answer, Long>,
        JpaSpecificationExecutor<Answer> {

    boolean existsByQuestion(Question question);

    // JPA 쿼리 메서드를 사용하여 특정 사용자의 특정 날짜 범위 내 답변 개수를 조회
    long countByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startOfDay,
            LocalDateTime endOfDay);

    // 꼬리질문이 아닌 일반 질문에 대한 답변만 카운트 (일일 제한에 사용)
    long countByUserIdAndFollowUpQuestionIsNullAndCreatedAtBetween(Long userId, LocalDateTime startOfDay,
            LocalDateTime endOfDay);

    long countByUserId(Long userId);

    List<Answer> findByUserIdAndCreatedAtGreaterThanEqual(Long userId, LocalDateTime startOfDay);
}

