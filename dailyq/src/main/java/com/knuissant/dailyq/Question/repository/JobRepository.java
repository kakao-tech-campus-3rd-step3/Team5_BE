package com.knuissant.dailyq.Question.repository;

import com.knuissant.dailyq.Question.entity.Job;
import com.knuissant.dailyq.Question.enums.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {
    Optional<Job> findByName(JobRole name);
}


