package com.knuissant.dailyq.Question.repository;

import com.knuissant.dailyq.Question.entity.UserFlowProgress;
import com.knuissant.dailyq.Question.entity.UserFlowProgressId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFlowProgressRepository extends JpaRepository<UserFlowProgress, UserFlowProgressId> {}


