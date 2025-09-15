package com.knuissant.dailyq.dto;

import com.knuissant.dailyq.domain.questions.QuestionType;
import java.time.LocalDate;

public record AnswerSearchConditionRequest(

        LocalDate date,

        Long jobId,

        QuestionType questionType,

        Boolean starred,

        Integer level,

        String sortOrder
) {

}
