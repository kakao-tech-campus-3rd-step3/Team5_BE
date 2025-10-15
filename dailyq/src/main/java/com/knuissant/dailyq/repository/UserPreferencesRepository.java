package com.knuissant.dailyq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knuissant.dailyq.domain.users.UserPreferences;
import com.knuissant.dailyq.domain.jobs.Job;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    boolean existsByUserJob(Job userJob);
}