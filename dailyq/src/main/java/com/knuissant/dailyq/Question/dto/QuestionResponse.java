package com.knuissant.dailyq.Question.dto;

import com.knuissant.dailyq.Question.enums.FlowPhase;
import com.knuissant.dailyq.Question.enums.JobRole;
import com.knuissant.dailyq.Question.enums.QuestionType;

public class QuestionResponse {
    private Long id;
    private String content;
    private JobRole jobRole;
    private QuestionType type;
    private FlowPhase flowPhase;

    public QuestionResponse(Long id, String content, JobRole jobRole, QuestionType type, FlowPhase flowPhase) {
        this.id = id;
        this.content = content;
        this.jobRole = jobRole;
        this.type = type;
        this.flowPhase = flowPhase;
    }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public JobRole getJobRole() { return jobRole; }
    public QuestionType getType() { return type; }
    public FlowPhase getFlowPhase() { return flowPhase; }
}


