package com.knuissant.dailyq.archive.dto;

import com.knuissant.dailyq.answer.Answer;
import java.time.LocalDateTime;

public class ArchiveResponse {

  //목록 조회 시 사용
  public record Summary(
      Long answerId,
      String questionContent,
      LocalDateTime answeredAt,
      int difficulty,
      boolean starred
  ) {
    public static Summary from(Answer answer) {
      return new Summary(
          answer.getId(),
          answer.getQuestion().getContent(),
          answer.getAnsweredAt(),
          answer.getDifficulty(),
          answer.isStarred()
      );
    }
  }

    //상세 조회 시 사용
  public record Detail(
      Long answerId,
      String questionContent,
      String answerText,
      String feedback,
      LocalDateTime answeredAt,
      int difficulty, // 요청에 따라 int 타입 유지
      boolean starred,
      String jobName
  ) {

      public static Detail from(Answer answer) {
        return new Detail(
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
