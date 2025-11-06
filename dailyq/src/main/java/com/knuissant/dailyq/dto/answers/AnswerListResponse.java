package com.knuissant.dailyq.dto.answers;

import java.time.LocalDateTime;
import java.util.List;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.questions.QuestionType;

public record AnswerListResponse(List<Summary> summaries) {

    public record Summary(
            Long answerId,
            Long questionId,
            String questionText,
            QuestionType questionType,
            Integer level,
            Boolean starred,
            LocalDateTime createdAt,
            Boolean isFollowup
    ) {

        public static Summary from(Answer answer) {
            boolean isFollowUp = answer.getFollowUpQuestion() != null;

            if (isFollowUp) {
                return new Summary(
                        answer.getId(),
                        answer.getFollowUpQuestion().getId(),
                        answer.getFollowUpQuestion().getQuestionText(),
                        QuestionType.TECH,
                        answer.getLevel(),
                        answer.getStarred(),
                        answer.getCreatedAt(),
                        true
                );
            } else {
                return new Summary(
                        answer.getId(),
                        answer.getQuestion().getId(),
                        answer.getQuestion().getQuestionText(),
                        answer.getQuestion().getQuestionType(),
                        answer.getLevel(),
                        answer.getStarred(),
                        answer.getCreatedAt(),
                        false
                );
            }
        }
    }

    public record CursorResult<T>(
            List<T> items,
            Long nextId,
            LocalDateTime nextCreatedAt,
            boolean hasNext
    ) {

    }
}