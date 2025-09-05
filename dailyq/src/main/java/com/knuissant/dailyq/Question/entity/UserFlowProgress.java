package com.knuissant.dailyq.Question.entity;

import com.knuissant.dailyq.Question.enums.FlowPhase;
import com.knuissant.dailyq.Question.enums.JobRole;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_flow_progress")
public class UserFlowProgress {

    @EmbeddedId
    private UserFlowProgressId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "last_phase", nullable = false, length = 20)
    private FlowPhase lastPhase;

    @Column(name = "last_assigned_date")
    private LocalDate lastAssignedDate;

    protected UserFlowProgress() {}

    public UserFlowProgress(Long userId, JobRole jobRole, FlowPhase lastPhase, LocalDate lastAssignedDate) {
        this.id = new UserFlowProgressId(userId, jobRole);
        this.lastPhase = lastPhase;
        this.lastAssignedDate = lastAssignedDate;
    }

    public FlowPhase getLastPhase() { return lastPhase; }
    public LocalDate getLastAssignedDate() { return lastAssignedDate; }
    public UserFlowProgressId getId() { return id; }

    public void markAssignedToday(FlowPhase phase) {
        this.lastPhase = phase;
        this.lastAssignedDate = LocalDate.now();
    }
}


