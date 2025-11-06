package com.knuissant.dailyq.repository;

import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.knuissant.dailyq.domain.feedbacks.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Optional<Feedback> findByAnswerId(Long answerId);

    @EntityGraph(attributePaths = {"answer", "answer.question", "answer.followUpQuestion"})
    Optional<Feedback> findWithDetailsById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM Feedback f WHERE f.id = :feedbackId")
    Optional<Feedback> findByIdForUpdate(@Param("feedbackId") Long feedbackId);
}
