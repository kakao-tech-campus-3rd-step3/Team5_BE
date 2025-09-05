package com.knuissant.dailyq.Question.entity;

import com.knuissant.dailyq.Question.enums.JobRole;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserFlowProgressId implements Serializable {

    @Column(name = "u_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_role", length = 30)
    private JobRole jobRole;

    protected UserFlowProgressId() {}

    public UserFlowProgressId(Long userId, JobRole jobRole) {
        this.userId = userId;
        this.jobRole = jobRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFlowProgressId that = (UserFlowProgressId) o;
        return Objects.equals(userId, that.userId) && jobRole == that.jobRole;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, jobRole);
    }
}


