package com.knuissant.dailyq.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.questions.FollowUpQuestion;

public interface FollowUpQuestionRepository extends JpaRepository<FollowUpQuestion, Long> {

    /**
     * 사용자의 미답변 꼬리질문을 생성일 기준 오름차순으로 첫 번째만 조회
     */
    Optional<FollowUpQuestion> findFirstByUserIdAndIsAnsweredFalseOrderByCreatedAtAsc(Long userId);

    /**
     * 특정 답변에 대한 꼬리질문이 존재하는지 확인
     */
    boolean existsByAnswer(Answer answer);

    /**
     * 특정 답변에 대한 모든 꼬리질문 조회
     */
    List<FollowUpQuestion> findByAnswer(Answer answer);

    /**
     * 답변이 꼬리질문에 대한 답변인지 확인 (꼬리질문의 꼬리질문 방지용)
     */
    @Query("SELECT COUNT(fq) > 0 FROM FollowUpQuestion fq WHERE fq.answer = :answer")
    boolean isAnswerToFollowUpQuestion(@Param("answer") Answer answer);
}
