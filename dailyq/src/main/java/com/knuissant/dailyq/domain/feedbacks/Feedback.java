package com.knuissant.dailyq.domain.feedbacks;

import java.time.LocalDateTime;

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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.companies.Company;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_type", nullable = false, length = 20)
    private FeedbackType feedbackType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private FeedbackStatus status;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(name = "latency_ms")
    private Long latencyMs;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false)
    private LocalDateTime updatedAt;

    public static Feedback createGeneralFeedback(Answer answer, FeedbackStatus status) {
        return Feedback.builder()
                .answer(answer)
                .feedbackType(FeedbackType.GENERAL)
                .status(status)
                .build();
    }

    public static Feedback createCultureFitFeedback(Answer answer, Company company, FeedbackStatus status) {
        return Feedback.builder()
                .answer(answer)
                .company(company)
                .feedbackType(FeedbackType.CULTURE_FIT)
                .status(status)
                .build();
    }

    public void startProcessing() {
        this.status = FeedbackStatus.PROCESSING;
    }

    public void updateSuccess(String content, Long latencyMs) {
        this.status = FeedbackStatus.DONE;
        this.content = content;
        this.latencyMs = latencyMs;
    }

    public void updateFailure() {
        this.status = FeedbackStatus.FAILED;
    }
}


