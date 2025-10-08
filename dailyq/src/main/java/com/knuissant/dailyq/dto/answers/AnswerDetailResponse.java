package com.knuissant.dailyq.dto.answers;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.feedbacks.Feedback;
import com.knuissant.dailyq.domain.feedbacks.FeedbackStatus;
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.questions.QuestionType;
import com.knuissant.dailyq.dto.feedbacks.FeedbackResponse;
import com.knuissant.dailyq.exception.InfraException;

@Slf4j
public record AnswerDetailResponse(
        Long answerId,
        QuestionSummary question,
        String answerText,
        String memo,
        Integer level,
        Boolean starred,
        LocalDateTime createdAt,
        FeedbackDetail feedback
) {

    public record QuestionSummary(Long questionId, QuestionType questionType, String questionText
    ) {

        public static QuestionSummary from(Question question) {
            return new QuestionSummary(
                    question.getId(),
                    question.getQuestionType(),
                    question.getQuestionText()
            );
        }
    }

    public record FeedbackDetail(FeedbackStatus status, FeedbackResponse content, LocalDateTime updatedAt
    ) {

        public static FeedbackDetail from(Feedback feedback, ObjectMapper objectMapper) {
            try {
                FeedbackResponse feedbackContent = FeedbackResponse.from(
                        feedback.getContent(),
                        objectMapper
                );
                return new FeedbackDetail(
                        feedback.getStatus(),
                        feedbackContent,
                        feedback.getUpdatedAt()
                );
            } catch (InfraException e) {
                log.error("Failed to parse feedback content JSON: {}", feedback.getContent(), e);
                return new FeedbackDetail(
                        feedback.getStatus(),
                        null,
                        feedback.getUpdatedAt()
                );

            }
        }
    }

    public static AnswerDetailResponse of(Answer answer, Feedback feedback, ObjectMapper objectMapper) {
        return new AnswerDetailResponse(
                answer.getId(),
                QuestionSummary.from(answer.getQuestion()),
                answer.getAnswerText(),
                answer.getMemo(),
                answer.getLevel(),
                answer.getStarred(),
                answer.getCreatedAt(),
                (feedback != null) ? FeedbackDetail.from(feedback, objectMapper) : null
        );
    }
}
