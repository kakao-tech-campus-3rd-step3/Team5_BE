package com.knuissant.dailyq.dto.answers;

import java.time.LocalDateTime;
import java.util.List;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.questions.FlowPhase;
import com.knuissant.dailyq.domain.questions.QuestionType;

public record AnswerListResponse(List<Summary> summaries) {

    public record Summary(
            Long answerId,
            Long questionId,
            String questionText,
            QuestionType questionType,
            FlowPhase flowPhase,
            Integer level,
            Boolean starred,
            LocalDateTime answeredTime
    ) {

        public static Summary from(Answer answer) {
            return new Summary(
                    answer.getId(),
                    answer.getQuestion().getId(),
                    answer.getQuestion().getQuestionText(),
                    answer.getQuestion().getQuestionType(),
                    null, // flow_phase는 FE와 협의 필요
                    answer.getLevel(),
                    answer.getStarred(),
                    answer.getAnsweredTime()
            );
        }
    }

    public record CursorResult<T>(
            List<T> items,

            String nextCursor,

            boolean hasNext
    ) {

    }
}
