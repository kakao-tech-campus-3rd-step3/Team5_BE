package com.knuissant.dailyq.dto.users;

import java.time.LocalTime;

import com.knuissant.dailyq.domain.questions.QuestionMode;
import com.knuissant.dailyq.domain.users.UserResponseType;

// 사용자 환경설정 응답 DTO
public record UserPreferencesResponse(
        Integer dailyQuestionLimit,
        QuestionMode questionMode,
        UserResponseType answerType,
        Integer timeLimitSeconds,
        LocalTime notifyTime,
        Boolean allowPush,
        Long userJobId
) { }