package com.knuissant.dailyq.dto;

import com.knuissant.dailyq.domain.questions.QuestionMode;
import com.knuissant.dailyq.domain.users.UserResponseType;

import java.time.LocalTime;

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