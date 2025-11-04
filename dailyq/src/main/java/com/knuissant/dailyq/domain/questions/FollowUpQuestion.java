package com.knuissant.dailyq.domain.questions;

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

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.common.BaseTimeEntity;
import com.knuissant.dailyq.domain.users.User;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "follow_up_questions", indexes = {
        @Index(name = "idx_followup_user_answered", columnList = "user_id, is_answered, created_at ASC"),
        @Index(name = "idx_followup_user_desc", columnList = "user_id, created_at DESC"),
        @Index(name = "idx_followup_answer", columnList = "answer_id")
})
public class FollowUpQuestion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_up_question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;

    @Column(name = "question_text", columnDefinition = "MEDIUMTEXT", nullable = false)
    private String questionText;

    @Column(name = "is_answered", nullable = false)
    @Builder.Default
    private Boolean isAnswered = false;

    public static FollowUpQuestion create(User user, Answer answer, String questionText) {
        return FollowUpQuestion.builder()
                .user(user)
                .answer(answer)
                .questionText(questionText)
                .build();
    }

    public void markAsAnswered() {
        this.isAnswered = true;
    }
}
