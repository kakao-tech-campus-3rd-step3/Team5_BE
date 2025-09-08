package com.knuissant.dailyq.repository;

import com.knuissant.dailyq.domain.answers.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query(value = "SELECT COUNT(*) FROM answers WHERE user_id = :userId AND answered_date = CURDATE()", nativeQuery = true)
    long countTodayByUserId(@Param("userId") Long userId);
}


