package com.knuissant.dailyq.dto;

import com.knuissant.dailyq.domain.questions.QuestionMode;
import com.knuissant.dailyq.domain.users.UserPreferences;
import com.knuissant.dailyq.domain.users.UserResponseType;
import lombok.Builder;

import java.time.LocalTime;

// 사용자 환경설정 응답 DTO
@Builder
public record UserPreferencesResponse(
        Integer dailyQuestionLimit,
        QuestionMode questionMode,
        UserResponseType answerType,
        Integer timeLimitSeconds,
        LocalTime notifyTime,
        Boolean allowPush,
        Long userJobId
) {
    public static UserPreferencesResponse from(UserPreferences preferences) {
        return UserPreferencesResponse.builder()
                .dailyQuestionLimit(preferences.getDailyQuestionLimit())
                .questionMode(preferences.getQuestionMode())
                .answerType(preferences.getUserResponseType())
                .timeLimitSeconds(preferences.getTimeLimitSeconds())
                .notifyTime(preferences.getNotifyTime())
                .allowPush(preferences.getAllowPush())
                .userJobId(preferences.getUserJob() != null ? preferences.getUserJob().getId() : null)
                .build();
    }
}
