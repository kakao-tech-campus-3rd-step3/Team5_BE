package com.knuissant.dailyq.dto.users;

import com.knuissant.dailyq.domain.questions.QuestionMode;

public record UserPreferencesUpdateRequest(
        Integer dailyQuestionLimit,
        QuestionMode questionMode,
        Integer timeLimitSeconds,
        Boolean allowPush
) {

}