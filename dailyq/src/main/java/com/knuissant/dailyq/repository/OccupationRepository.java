package com.knuissant.dailyq.repository;

import com.knuissant.dailyq.domain.jobs.Occupation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OccupationRepository extends JpaRepository<Occupation, Long> {
}