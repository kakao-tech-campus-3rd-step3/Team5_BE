package com.knuissant.dailyq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knuissant.dailyq.domain.jobs.Job;
import com.knuissant.dailyq.domain.jobs.Occupation;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByOccupationId(Long occupationId);

    boolean existsByName(String name);
    boolean existsByOccupation(Occupation occupation);
}
