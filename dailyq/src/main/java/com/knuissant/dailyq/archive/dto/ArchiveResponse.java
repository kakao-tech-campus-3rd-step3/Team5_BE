package com.knuissant.dailyq.archive.dto;

import com.knuissant.dailyq.answer.Answer;
import java.time.LocalDateTime;

public record ArchiveResponse(

    Long answerId,
    String questionContent,
    String answerText,
    String feedback,
    LocalDateTime answeredAt,
    int difficulty,
    boolean starred,
    String jobName
) {

  public static ArchiveResponse from(Answer answer) {
    return new ArchiveResponse(
        answer.getId(),
        answer.getQuestion().getContent(),
        answer.getAnswerText(),
        answer.getFeedback(),
        answer.getAnsweredAt(),
        answer.getDifficulty(),
        answer.isStarred(),
        answer.getQuestion().getJob().getJobName()
    );
  }
}
