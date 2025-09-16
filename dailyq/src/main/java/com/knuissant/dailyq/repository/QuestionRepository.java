package com.knuissant.dailyq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.questions.QuestionType;

import com.knuissant.dailyq.constants.QuestionConstants;
import com.knuissant.dailyq.domain.questions.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * 활성화된 특정 타입의 질문들을 조회
     */
    List<Question> findByEnabledTrueAndQuestionType(QuestionType questionType);

    /**
     * 특정 직군과 연결된 TECH 타입의 활성화된 질문들을 조회
     */
    List<Question> findByEnabledTrueAndQuestionTypeAndJobsId(QuestionType questionType, Long jobId);
}
