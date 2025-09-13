package com.knuissant.dailyq.domain.users;

import com.knuissant.dailyq.domain.jobs.Job;
import com.knuissant.dailyq.domain.questions.QuestionMode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "user_preferences")
public class UserPreferences {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_prefs_user"))
    private User user;

    @Column(name = "daily_question_limit", nullable = false)
    private Integer dailyQuestionLimit;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_mode", nullable = false, length = 10)
    private QuestionMode questionMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_response_type", nullable = false, length = 10)
    private UserResponseType userResponseType;

    @Column(name = "time_limit_seconds")
    private Integer timeLimitSeconds;

    @Column(name = "notify_time")
    private LocalTime notifyTime;

    @Column(name = "allow_push", nullable = false)
    private Boolean allowPush;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_job", nullable = false)
    private Job userJob;

    public void updatePreferences(Integer dailyQuestionLimit, QuestionMode questionMode, UserResponseType answerType, Integer timeLimitSeconds, Boolean allowPush) {
        this.dailyQuestionLimit = dailyQuestionLimit;
        this.questionMode = questionMode;
        this.userResponseType = answerType;
        this.timeLimitSeconds = timeLimitSeconds;
        this.allowPush = allowPush;
    }

    public void changeJob(Job newJob) {
        this.userJob = newJob;
    }
}