package com.knuissant.dailyq.dto.answers;

import java.time.LocalDate;

import com.knuissant.dailyq.domain.questions.QuestionType;
public record AnswerSearchConditionRequest(

        LocalDate date,

        Long jobId,

        QuestionType questionType,

        Boolean starred,

        Integer level,

        String sortOrder
) {

}
