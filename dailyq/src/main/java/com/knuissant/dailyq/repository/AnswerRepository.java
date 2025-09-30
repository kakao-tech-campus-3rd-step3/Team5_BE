package com.knuissant.dailyq.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.dto.rivals.RivalProfileResponse;
import com.knuissant.dailyq.dto.rivals.RivalProfileResponse.DailySolveCount;

public interface AnswerRepository extends JpaRepository<Answer, Long>,
        JpaSpecificationExecutor<Answer> {

    // JPA 쿼리 메서드를 사용하여 특정 사용자의 특정 날짜 범위 내 답변 개수를 조회
    long countByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startOfDay,
            LocalDateTime endOfDay);

    long countByUserId(Long userId);

    @Query(value = """
            SELECT DATE(a.created_at) as date, COUNT(*) as count 
            FROM answers a 
            WHERE a.user_id = :userId 
              AND a.created_at >= :startDate 
            GROUP BY DATE(a.created_at)
            ORDER BY date DESC
            """, nativeQuery = true)
    List<Object[]> findDailySolveCountsByUserId(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate);
}

