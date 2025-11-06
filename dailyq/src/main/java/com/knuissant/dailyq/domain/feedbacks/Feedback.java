package com.knuissant.dailyq.domain.feedbacks;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.common.BaseTimeEntity;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "feedbacks", indexes = {
        @Index(name = "idx_feedback_answer_status", columnList = "answer_id, status")
})
public class Feedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private FeedbackStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private FeedbackContent content;

    @Column(name = "latency_ms")
    private Long latencyMs;

    public static Feedback create(Answer answer, FeedbackStatus status) {
        return Feedback.builder()
                .answer(answer)
                .status(status)
                .build();
    }

    public boolean isDone() {
        return status == FeedbackStatus.DONE;
    }

    public boolean isUpdatable() {
        return status == FeedbackStatus.PENDING || status == FeedbackStatus.FAILED;
    }

    public void startProcessing() {
        this.status = FeedbackStatus.PROCESSING;
    }

    public void updateSuccess(FeedbackContent content, Long latencyMs) {
        this.status = FeedbackStatus.DONE;
        this.content = content;
        this.latencyMs = latencyMs;
    }

    public void updateFailure() {
        this.status = FeedbackStatus.FAILED;
    }
}


