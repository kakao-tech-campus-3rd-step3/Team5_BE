package com.knuissant.dailyq.domain.answers;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.users.User;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "answers", indexes = {
        @Index(name = "idx_answers_user_time", columnList = "user_id, created_at DESC"),
        @Index(name = "idx_answers_q_time", columnList = "question_id, created_at DESC")
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

    @Column(name = "answer_text", columnDefinition = "MEDIUMTEXT", nullable = false)
    private String answerText;

    @Column
    private Integer level;

    @Column(nullable = false, insertable = false)
    private Boolean starred;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "memo", columnDefinition = "MEDIUMTEXT")
    private String memo;

    public static Answer create(User user, Question question, String answerText) {
        return Answer.builder()
                .user(user)
                .question(question)
                .answerText(answerText)
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
}


