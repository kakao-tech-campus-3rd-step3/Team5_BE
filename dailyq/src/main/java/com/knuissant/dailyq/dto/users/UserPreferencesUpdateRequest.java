package com.knuissant.dailyq.dto.users;

import com.knuissant.dailyq.domain.questions.QuestionMode;
import com.knuissant.dailyq.domain.users.UserResponseType;

// 사용자 환경 설정 수정 요청에 사용되는 DTO
public record UserPreferencesUpdateRequest(
        Integer dailyQuestionLimit,
        QuestionMode questionMode,
        UserResponseType answerType,
        Integer timeLimitSeconds,
        Boolean allowPush
) {}