package com.knuissant.dailyq.Question.entity;

import com.knuissant.dailyq.Question.enums.FlowPhase;
import com.knuissant.dailyq.Question.enums.QuestionType;
import com.knuissant.dailyq.Question.entity.vo.QuestionContent;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "questions",
        indexes = {
                @Index(name = "idx_questions_job_type", columnList = "j_id,type"),
                @Index(name = "idx_questions_job_phase", columnList = "j_id,flow_phase")
        })
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "q_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "j_id", nullable = false)
    private Job job;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private QuestionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "flow_phase", length = 20)
    private FlowPhase flowPhase;

    @Column(name = "topic", length = 50)
    private String topic;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    protected Question() {}

    public Question(Job job, QuestionContent content, QuestionType type, FlowPhase flowPhase, String topic) {
        this.job = job;
        this.content = content.getValue();
        this.type = type;
        this.flowPhase = flowPhase;
        this.topic = topic;
    }

    @PreUpdate
    public void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public Job getJob() { return job; }
    public String getContent() { return content; }
    public QuestionType getType() { return type; }
    public FlowPhase getFlowPhase() { return flowPhase; }
}


