package com.knuissant.dailyq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.knuissant.dailyq.domain.answers.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long>,
        JpaSpecificationExecutor<Answer> {

    @Query(value = "SELECT COUNT(*) FROM answers WHERE user_id = :userId AND DATE(answered_time) = CURDATE()", nativeQuery = true)
    long countTodayByUserId(@Param("userId") Long userId);
}

