package com.knuissant.dailyq.Question.repository;

import com.knuissant.dailyq.Question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = """
            SELECT q.* FROM questions q
            JOIN jobs j ON j.j_id = q.j_id
            LEFT JOIN answers a ON a.q_id = q.q_id AND a.u_id = :userId
            WHERE j.name = :jobRole
              AND q.type = 'TECH'
              AND a.a_id IS NULL
            ORDER BY RAND()
            LIMIT :count
            """, nativeQuery = true)
    List<Question> findRandomTechExcludingSolved(@Param("jobRole") String jobRole,
                                                 @Param("userId") long userId,
                                                 @Param("count") int count);

    @Query(value = """
            SELECT q.* FROM questions q
            JOIN jobs j ON j.j_id = q.j_id
            LEFT JOIN answers a ON a.q_id = q.q_id AND a.u_id = :userId
            WHERE j.name = :jobRole
              AND q.flow_phase = :phase
              AND a.a_id IS NULL
            ORDER BY RAND()
            LIMIT 1
            """, nativeQuery = true)
    List<Question> findRandomByFlowPhaseExcludingSolved(@Param("jobRole") String jobRole,
                                                        @Param("phase") String phase,
                                                        @Param("userId") long userId);
}


