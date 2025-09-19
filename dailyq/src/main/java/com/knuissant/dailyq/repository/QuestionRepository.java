package com.knuissant.dailyq.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.knuissant.dailyq.constants.QuestionConstants;
import com.knuissant.dailyq.domain.questions.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = "SELECT COUNT(*) FROM questions q " +
        "WHERE q.enabled = 1 AND q.question_type = :questionType " +
        "AND q.question_id NOT IN (" +
        "  SELECT a.question_id FROM answers a WHERE a.user_id = :userId" +
        ")", nativeQuery = true)
    long countAvailableQuestions(@Param("questionType") String questionType, @Param("userId") Long userId);

    @Query(value = "SELECT q.* FROM questions q " +
         "WHERE q.enabled = 1 AND q.question_type = :questionType " +
        "AND q.question_id NOT IN (" +
        "  SELECT a.question_id FROM answers a " +
        "  WHERE a.user_id = :userId" +
        ") " +
        "ORDER BY q.question_id LIMIT 1 OFFSET :offset", nativeQuery = true)
    Optional<Question> findRandomByTypeWithOffset(@Param("questionType") String questionType, @Param("userId") Long userId, @Param("offset") int offset);

    @Query(value = "SELECT COUNT(*) FROM questions q " +
        "JOIN question_jobs qj ON q.question_id = qj.question_id " +
        "WHERE q.enabled = 1 AND q.question_type = '" + QuestionConstants.QUESTION_TYPE_TECH + "' AND qj.job_id = :jobId " +
        "AND q.question_id NOT IN (" +
        "  SELECT a.question_id FROM answers a WHERE a.user_id = :userId" +
        ")", nativeQuery = true)
    long countAvailableTechQuestions(@Param("jobId") Long jobId, @Param("userId") Long userId);

    @Query(value = "SELECT q.* FROM questions q " +
        "JOIN question_jobs qj ON q.question_id = qj.question_id " +
        "WHERE q.enabled = 1 AND q.question_type = '" + QuestionConstants.QUESTION_TYPE_TECH + "' AND qj.job_id = :jobId " +
        "AND q.question_id NOT IN (" +
        "  SELECT a.question_id FROM answers a " +
        "  WHERE a.user_id = :userId" +
        ") " +
        "ORDER BY q.question_id LIMIT 1 OFFSET :offset", nativeQuery = true)
    Optional<Question> findRandomTechByJobIdWithOffset(@Param("jobId") Long jobId, @Param("userId") Long userId, @Param("offset") int offset);
}