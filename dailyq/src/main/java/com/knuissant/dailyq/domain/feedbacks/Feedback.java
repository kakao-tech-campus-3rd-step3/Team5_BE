package com.knuissant.dailyq.domain.feedbacks;

import com.knuissant.dailyq.domain.answers.Answer;
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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "feedbacks", indexes = {
        @Index(name = "idx_feedback_answer_status", columnList = "answer_id, status")
})
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private FeedbackStatus status;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(name = "latency_ms")
    private Long latencyMs;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void updateStatus(FeedbackStatus status) {
        this.status = status;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateLatencyMs(Long latencyMs) {
        this.latencyMs = latencyMs;
    }
}


