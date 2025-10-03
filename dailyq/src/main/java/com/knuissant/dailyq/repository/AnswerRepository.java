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

    @Query("SELECT new com.knuissant.dailyq.dto.rivals.RivalProfileResponse.DailySolveCount(CAST(a.createdAt AS LocalDate), COUNT(a)) " +
            "FROM Answer a " +
            "WHERE a.user.id = :userId AND a.createdAt >= :startDate " +
            "GROUP BY CAST(a.createdAt AS LocalDate)")
    List<RivalProfileResponse.DailySolveCount> findDailySolveCountsByUserId(@Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate);
}

