package com.knuissant.dailyq.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.questions.QuestionType;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT COUNT(q) FROM Question q " +
        "WHERE q.enabled = true AND q.questionType = :questionType " +
        "AND q.id NOT IN (" +
        "  SELECT a.question.id FROM Answer a WHERE a.user.id = :userId" +
        ")")
    long countAvailableQuestions(@Param("questionType") QuestionType questionType, @Param("userId") Long userId);

    @Query("SELECT q FROM Question q " +
        "WHERE q.enabled = true AND q.questionType = :questionType " +
        "AND q.id NOT IN (" +
        "  SELECT a.question.id FROM Answer a WHERE a.user.id = :userId" +
        ") " +
        "ORDER BY q.id")
    List<Question> findAvailableQuestionsByType(@Param("questionType") QuestionType questionType, @Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(q) FROM Question q JOIN q.jobs j " +
        "WHERE q.enabled = true AND q.questionType = com.knuissant.dailyq.domain.questions.QuestionType.TECH AND j.id = :jobId " +
        "AND q.id NOT IN (" +
        "  SELECT a.question.id FROM Answer a WHERE a.user.id = :userId" +
        ")")
    long countAvailableTechQuestions(@Param("jobId") Long jobId, @Param("userId") Long userId);

    @Query("SELECT q FROM Question q JOIN q.jobs j " +
        "WHERE q.enabled = true AND q.questionType = com.knuissant.dailyq.domain.questions.QuestionType.TECH AND j.id = :jobId " +
        "AND q.id NOT IN (" +
        "  SELECT a.question.id FROM Answer a WHERE a.user.id = :userId" +
        ") " +
        "ORDER BY q.id")
    List<Question> findAvailableTechQuestionsByJobId(@Param("jobId") Long jobId, @Param("userId") Long userId, Pageable pageable);
}