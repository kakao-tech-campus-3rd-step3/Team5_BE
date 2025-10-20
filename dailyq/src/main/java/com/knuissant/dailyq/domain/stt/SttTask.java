package com.knuissant.dailyq.domain.stt;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.knuissant.dailyq.domain.answers.Answer;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "stt_tasks")
public class SttTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stt_task_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "answer_id", nullable = false, unique = true)
    private Answer answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private SttTaskStatus status;

    @Column(name = "audio_url", nullable = false, length = 2048)
    private String audioUrl;

    @Column(name = "error_message", length = 512)
    private String errorMessage;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false)
    private LocalDateTime updatedAt;

    public static SttTask create(Answer answer, String audioUrl) {
        return SttTask.builder()
                .answer(answer)
                .status(SttTaskStatus.PENDING)
                .audioUrl(audioUrl)
                .build();
    }

    public void complete() {
        this.status = SttTaskStatus.COMPLETED;
    }

    public void fail(String errorMessage) {
        this.status = SttTaskStatus.FAILED;
        this.errorMessage = errorMessage;
    }
}
