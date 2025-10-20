package com.knuissant.dailyq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knuissant.dailyq.domain.stt_tasks.SttTask;

@Repository
public interface SttTaskRepository extends JpaRepository<SttTask, Long> {

}
