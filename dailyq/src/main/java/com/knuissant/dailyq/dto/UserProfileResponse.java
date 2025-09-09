package com.knuissant.dailyq.dto;

import com.knuissant.dailyq.domain.questions.QuestionMode;
import com.knuissant.dailyq.domain.users.UserResponseType;
import lombok.Builder;

import java.util.List;

// 사용자 프로필 응답에 사용되는 DTO
@Builder
public record UserProfileResponse(
        Long userId,
        String email,
        String name,
        Integer streak,
        Boolean solvedToday,
        UserProfileResponse.PreferencesDto preferences,
        List<JobDto> jobs
) {
    public record PreferencesDto(
            Integer dailyQuestionLimit,
            QuestionMode questionMode,
            UserResponseType answerType,
            Integer timeLimitSeconds,
            Boolean allowPush
    ) {}

    public record JobDto(
            Long jobId,
            String jobName
    ) {}
}