package com.knuissant.dailyq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knuissant.dailyq.domain.jobs.Occupation;

@Repository
public interface OccupationRepository extends JpaRepository<Occupation, Long> {
}
