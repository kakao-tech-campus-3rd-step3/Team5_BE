package com.knuissant.dailyq.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knuissant.dailyq.domain.feedbacks.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Optional<Feedback> findByAnswerId(Long answerId);

    @EntityGraph(attributePaths = {"answer", "answer.question"})
    Optional<Feedback> findWithDetailsById(Long id);
}
