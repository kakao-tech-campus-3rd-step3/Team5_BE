package com.knuissant.dailyq.domain.users;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import com.knuissant.dailyq.domain.jobs.Job;
import com.knuissant.dailyq.domain.questions.QuestionMode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 팩토리 메서드에서 사용할 private 생성자
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

    public static UserPreferences createDefault(User user, Job defaultJob) {
        UserPreferences preferences = new UserPreferences();
        preferences.user = user;
        preferences.dailyQuestionLimit = 1;
        preferences.questionMode = QuestionMode.TECH;
        preferences.userResponseType = UserResponseType.TEXT;
        preferences.timeLimitSeconds = 180;
        preferences.allowPush = false;
        preferences.userJob = defaultJob;
        return preferences;
    }

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
