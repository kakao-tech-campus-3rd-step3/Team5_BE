package com.knuissant.dailyq.domain.questions;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.knuissant.dailyq.domain.jobs.Job;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, columnDefinition = "VARCHAR(20)")
    private QuestionType questionType;

    @Column(name = "question_text", columnDefinition = "MEDIUMTEXT", nullable = false)
    private String questionText;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "question_jobs",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "job_id")
    )
    @Builder.Default
    private Set<Job> jobs = new LinkedHashSet<>();

    public static Question create(String text, QuestionType type, Set<Job> jobs) {
        Question question = new Question();
        question.questionText = text;
        question.questionType = type;
        question.jobs = jobs;
        question.enabled = true; // 생성 시 기본값
        return question;
    }

    public void update(String text, QuestionType type, boolean enabled, Set<Job> jobs) {
        this.questionText = text;
        this.questionType = type;
        this.enabled = enabled;
        this.jobs = jobs;
    }
}


