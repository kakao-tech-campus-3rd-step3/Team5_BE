package com.knuissant.dailyq.repository;

import com.knuissant.dailyq.domain.questions.Question;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = "SELECT q.* FROM questions q " +
        "WHERE q.enabled = 1 AND q.question_type = :questionType " +
        "AND q.question_id NOT IN (" +
        "  SELECT a.question_id FROM answers a " +
        "  WHERE a.user_id = :userId AND a.answered_date = CURDATE()" +
        ") " +
        "ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Question> findRandomByTypeExcludingTodayAnswers(@Param("questionType") String questionType, @Param("userId") Long userId);

    @Query(value = "SELECT q.* FROM questions q " +
        "JOIN question_jobs qj ON q.question_id = qj.question_id " +
        "WHERE q.enabled = 1 AND q.question_type = 'TECH' AND qj.job_id = :jobId " +
        "AND q.question_id NOT IN (" +
        "  SELECT a.question_id FROM answers a " +
        "  WHERE a.user_id = :userId AND a.answered_date = CURDATE()" +
        ") " +
        "ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Question> findRandomTechByJobIdExcludingTodayAnswers(@Param("jobId") Long jobId, @Param("userId") Long userId);
}


