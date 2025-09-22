package com.knuissant.dailyq.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.knuissant.dailyq.domain.feedbacks.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Optional<Feedback> findByAnswerId(Long answerId);

    @Query("SELECT f FROM Feedback f JOIN FETCH f.answer a JOIN FETCH a.question q WHERE f.id = :id")
    Optional<Feedback> findByIdWithDetails(@Param("id") Long id);
}
