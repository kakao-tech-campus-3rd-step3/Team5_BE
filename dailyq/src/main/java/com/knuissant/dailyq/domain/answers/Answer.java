package com.knuissant.dailyq.domain.answers;

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
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.users.User;

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
@Table(name = "answers", indexes = {
        @Index(name = "idx_answers_user_time", columnList = "user_id, answered_time DESC"),
        @Index(name = "idx_answers_q_time", columnList = "question_id, answered_time DESC")
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

    @Column(nullable = false)
    private Boolean starred;

    @Column(name = "answered_time", nullable = false)
    private LocalDateTime answeredTime;

    // 생성 칼럼 (DB 계산) — 읽기 전용
    @Column(name = "answered_date", insertable = false, updatable = false)
    private LocalDate answeredDate;

    @Column(name = "memo", columnDefinition = "MEDIUMTEXT")
    private String memo;

    public void updateMemo(String memo) {
        this.memo = memo;
    }

    public void updateStarred(Boolean starred) {
        this.starred = starred;
    }
}


