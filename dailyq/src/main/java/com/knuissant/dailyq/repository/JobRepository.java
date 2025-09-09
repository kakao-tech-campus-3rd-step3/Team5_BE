package com.knuissant.dailyq.repository;

import com.knuissant.dailyq.domain.jobs.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByOccupationId(Long occupationId);
}