package com.knuissant.dailyq.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.knuissant.dailyq.domain.users.UserFlowProgress;

public interface UserFlowProgressRepository extends JpaRepository<UserFlowProgress, Long> {
}


