package com.knuissant.dailyq.domain.answers;

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

import com.knuissant.dailyq.domain.questions.FollowUpQuestion;
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "answers", indexes = {
        @Index(name = "idx_answers_user_time", columnList = "user_id, created_at DESC"),
        @Index(name = "idx_answers_q_time", columnList = "question_id, created_at DESC"),
        @Index(name = "idx_answers_followup", columnList = "follow_up_question_id")
})
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "answer_text", columnDefinition = "MEDIUMTEXT")
    private String answerText;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer_type", nullable = false, columnDefinition = "VARCHAR(20)")
    private AnswerType answerType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private AnswerStatus status;

    @Column
    private Integer level;

    @Column(nullable = false, insertable = false)
    private Boolean starred;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "memo", columnDefinition = "MEDIUMTEXT")
    private String memo;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "follow_up_question_id")
    private FollowUpQuestion followUpQuestion;

    public static Answer createTextAnswer(User user, Question question, String answerText) {
        return Answer.builder()
                .user(user)
                .question(question)
                .answerText(answerText)
                .answerType(AnswerType.TEXT)
                .status(AnswerStatus.COMPLETED)
                .build();
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }

    public void updateStarred(Boolean starred) {
        this.starred = starred;
    }

    public void updateLevel(Integer level) {
        this.level = level;
    }

    public void setFollowUpQuestion(FollowUpQuestion followUpQuestion) {
        this.followUpQuestion = followUpQuestion;
    }

    public void checkOwnership(Long userId) {
        if (!this.user.getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, "userId:", userId, "answerId:", this.id);
        }
    }
}


