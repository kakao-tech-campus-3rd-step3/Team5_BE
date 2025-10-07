package com.knuissant.dailyq.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.questions.QuestionType;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT MAX(q.id) FROM Question q JOIN q.jobs j " +
        "WHERE q.enabled = true AND q.questionType = com.knuissant.dailyq.domain.questions.QuestionType.TECH AND j.id = :jobId " +
        "AND NOT EXISTS (" +
        "  SELECT 1 FROM Answer a WHERE a.question.id = q.id AND a.user.id = :userId" +
        ")")
    Long findMaxAvailableTechQuestionId(@Param("jobId") Long jobId, @Param("userId") Long userId);

    @Query("SELECT MAX(q.id) FROM Question q " +
        "WHERE q.enabled = true AND q.questionType = :questionType " +
        "AND NOT EXISTS (" +
        "  SELECT 1 FROM Answer a WHERE a.question.id = q.id AND a.user.id = :userId" +
        ")")
    Long findMaxAvailableQuestionId(@Param("questionType") QuestionType questionType, @Param("userId") Long userId);

    @Query("SELECT q FROM Question q JOIN q.jobs j " +
        "WHERE q.enabled = true AND q.questionType = com.knuissant.dailyq.domain.questions.QuestionType.TECH AND j.id = :jobId " +
        "AND q.id >= :cursorId " +
        "AND NOT EXISTS (" +
        "  SELECT 1 FROM Answer a WHERE a.question.id = q.id AND a.user.id = :userId" +
        ") " +
        "ORDER BY q.id")
    List<Question> findAvailableTechQuestionsFromCursor(@Param("jobId") Long jobId, @Param("cursorId") Long cursorId, @Param("userId") Long userId, Pageable pageable);

    @Query("SELECT q FROM Question q " +
        "WHERE q.enabled = true AND q.questionType = :questionType " +
        "AND q.id >= :cursorId " +
        "AND NOT EXISTS (" +
        "  SELECT 1 FROM Answer a WHERE a.question.id = q.id AND a.user.id = :userId" +
        ") " +
        "ORDER BY q.id")
    List<Question> findAvailableQuestionsFromCursor(@Param("questionType") QuestionType questionType, @Param("cursorId") Long cursorId, @Param("userId") Long userId, Pageable pageable);
}