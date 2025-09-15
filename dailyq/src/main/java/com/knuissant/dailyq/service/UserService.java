package com.knuissant.dailyq.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.domain.users.UserPreferences;
import com.knuissant.dailyq.dto.UserProfileResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserPreferencesService userPreferencesService;

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        User user = findUserById(userId);
        UserPreferences preferences = userPreferencesService.findUserPreferencesByUserId(userId);

        List<UserProfileResponse.JobDto> jobDtos = (preferences.getUserJob() != null)
                ? List.of(new UserProfileResponse.JobDto(preferences.getUserJob().getId(), preferences.getUserJob().getName()))
                : Collections.emptyList();

        UserProfileResponse.PreferencesDto preferencesDto = new UserProfileResponse.PreferencesDto(
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

    public void updateUserName(Long userId, String newName) {
        User user = findUserById(userId);
        user.updateName(newName);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}