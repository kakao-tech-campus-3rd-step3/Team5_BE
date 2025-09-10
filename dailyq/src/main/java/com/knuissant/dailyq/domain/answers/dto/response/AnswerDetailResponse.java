package com.knuissant.dailyq.domain.answers.dto.response;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.feedbacks.Feedback;
import com.knuissant.dailyq.domain.feedbacks.FeedbackStatus;
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.questions.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "답변 상세 조회 응답 DTO")
public record AnswerDetailResponse(
    @Schema(description = "답변 id") Long answerId,
    @Schema(description = "질문 정보") QuestionSummary question,
    @Schema(description = "사용자 답변 내용") String answerText,
    @Schema(description = "사용자가 매긴 난이도") Integer level,
    @Schema(description = "즐찾 여부") Boolean starred,
    @Schema(description = "답변 시간") LocalDateTime answeredTime,
    @Schema(description = "AI 피드백 정보") FeedbackDetail feedback
) {

  @Schema(description = "질문 정보 DTO")
  public record QuestionSummary(
      @Schema(description = "질문 id") Long questionId,
      @Schema(description = "질문 유형") QuestionType questionType,
      @Schema(description = "질문 내용") String questionText
  ) {
    public static QuestionSummary from(Question question) {
      return new QuestionSummary(
          question.getId(),
          question.getQuestionType(),
          question.getQuestionText()
      );
    }
  }

  @Schema(description = "AI 피드백 상세 정보 DTO")
  public record FeedbackDetail(
      @Schema(description = "피드백 상태") FeedbackStatus status,
      @Schema(description = "피드백 내용") String feedbackText,
      @Schema(description = "피드백 업데이트 시간") LocalDateTime updatedAt
  ) {
    public static FeedbackDetail from(Feedback feedback) {
      return new FeedbackDetail(
          feedback.getStatus(),
          feedback.getFeedback(),
          feedback.getUpdatedAt()
      );
    }
  }

  public static AnswerDetailResponse of(Answer answer, Feedback feedback) {
    return new AnswerDetailResponse(
        answer.getId(),
        QuestionSummary.from(answer.getQuestion()),
        answer.getAnswerText(),
        answer.getLevel(),
        answer.getStarred(),
        answer.getAnsweredTime(),
        (feedback != null) ? FeedbackDetail.from(feedback) : null
    );
  }
}
