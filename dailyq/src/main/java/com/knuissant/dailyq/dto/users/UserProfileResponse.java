package com.knuissant.dailyq.dto.users;

import java.util.Collections;
import java.util.List;

import com.knuissant.dailyq.domain.questions.QuestionMode;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.domain.users.UserPreferences;
import com.knuissant.dailyq.domain.users.UserResponseType;

// 사용자 프로필 응답에 사용되는 DTO
public record UserProfileResponse(
        Long userId,
        String email,
        String name,
        Integer streak,
        Boolean solvedToday,
        PreferencesDto preferences,
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

    public static UserProfileResponse from(User user, UserPreferences preferences) {
        List<JobDto> jobDtos = (preferences.getUserJob() != null)
                ? List.of(new JobDto(preferences.getUserJob().getId(), preferences.getUserJob().getName()))
                : Collections.emptyList();

        PreferencesDto preferencesDto = new PreferencesDto(
                preferences.getDailyQuestionLimit(),
                preferences.getQuestionMode(),
                preferences.getUserResponseType(),
                preferences.getTimeLimitSeconds(),
                preferences.getAllowPush()
        );

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getStreak(),
                user.getSolvedToday(),
                preferencesDto,
                jobDtos
        );
    }
}
