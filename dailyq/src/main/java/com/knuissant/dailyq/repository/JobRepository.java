package com.knuissant.dailyq.repository;

import com.knuissant.dailyq.domain.jobs.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}


