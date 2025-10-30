package com.knuissant.dailyq.repository;

import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.knuissant.dailyq.domain.stt.SttTask;

@Repository
public interface SttTaskRepository extends JpaRepository<SttTask, Long> {

    Optional<SttTask> findByAnswerId(Long answerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SttTask> findByIdForUpdate(Long sttTaskId);
}
