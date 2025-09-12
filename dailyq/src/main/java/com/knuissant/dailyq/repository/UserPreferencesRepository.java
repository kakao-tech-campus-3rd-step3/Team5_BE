package com.knuissant.dailyq.repository;

import com.knuissant.dailyq.domain.users.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
}