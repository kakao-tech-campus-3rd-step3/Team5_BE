package com.knuissant.dailyq.service;

import com.knuissant.dailyq.domain.jobs.Job;
import com.knuissant.dailyq.domain.questions.QuestionMode;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.domain.users.UserPreferences;
import com.knuissant.dailyq.domain.users.UserResponseType;
import com.knuissant.dailyq.dto.UserJobsUpdateRequest;
import com.knuissant.dailyq.dto.UserPreferencesUpdateRequest;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.JobRepository;
import com.knuissant.dailyq.repository.UserPreferencesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserPreferencesService {

    private final UserPreferencesRepository userPreferencesRepository;
    private final JobRepository jobRepository;

    /**
     * Creates default preferences for a new user.
     * @param user The new user entity
     */
    public void createDefaultPreferences(User user) {
        Job defaultJob = jobRepository.findById(1L)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOB_NOT_FOUND));

        UserPreferences defaultPreferences = UserPreferences.builder()
                .user(user)
                .userId(user.getId())
                .dailyQuestionLimit(1)
                .questionMode(QuestionMode.TECH)
                .userResponseType(UserResponseType.TEXT)
                .timeLimitSeconds(180)
                .allowPush(false)
                .userJob(defaultJob)
                .build();
        userPreferencesRepository.save(defaultPreferences);
    }

    /**
     * Updates a user's preferences.
     * @param userId The ID of the user
     * @param request The preference update request DTO
     * @return The updated UserPreferences entity
     */
    public UserPreferences updateUserPreferences(Long userId, UserPreferencesUpdateRequest request) {
        UserPreferences preferences = findUserPreferencesByUserId(userId);

        // UserPreferencesUpdateRequest DTO 객체 자체를 인자로 전달합니다.
        preferences.updatePreferences(request);
        return preferences;
    }

    /**
     * Updates a user's representative job.
     * @param userId The ID of the user
     * @param request The job update request DTO
     */
    public void updateUserJob(Long userId, UserJobsUpdateRequest request) {
        UserPreferences preferences = findUserPreferencesByUserId(userId);
        Job job = jobRepository.findById(request.jobId())
                .orElseThrow(() -> new BusinessException(ErrorCode.JOB_NOT_FOUND));

        preferences.changeJob(job);
    }

    @Transactional(readOnly = true)
    public UserPreferences findUserPreferencesByUserId(Long userId) {
        return userPreferencesRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}

