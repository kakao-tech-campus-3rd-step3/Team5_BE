package com.knuissant.dailyq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.knuissant.dailyq.domain.answers.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long>,
        JpaSpecificationExecutor<Answer> {

    @Query(value = "SELECT COUNT(*) FROM answers WHERE user_id = :userId AND answered_time >= CURDATE() AND answered_time < DATE_ADD(CURDATE(), INTERVAL 1 DAY)", nativeQuery = true)
    long countTodayByUserId(@Param("userId") Long userId);

    List<Answer> findByUserId(Long userId);
}

