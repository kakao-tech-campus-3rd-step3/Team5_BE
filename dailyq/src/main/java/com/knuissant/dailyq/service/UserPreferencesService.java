package com.knuissant.dailyq.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.jobs.Job;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.domain.users.UserPreferences;
import com.knuissant.dailyq.dto.users.UserJobsUpdateRequest;
import com.knuissant.dailyq.dto.users.UserPreferencesResponse;
import com.knuissant.dailyq.dto.users.UserPreferencesUpdateRequest;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.JobRepository;
import com.knuissant.dailyq.repository.UserRepository;
import com.knuissant.dailyq.repository.UserPreferencesRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class UserPreferencesService {

    private final UserPreferencesRepository userPreferencesRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    private static final long DEFAULT_JOB_ID = 1L;

    public void createDefaultPreferences(User user) {
        Job defaultJob = jobRepository.findById(DEFAULT_JOB_ID)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOB_NOT_FOUND));

        UserPreferences defaultPreferences = UserPreferences.createDefault(user, defaultJob);
        userPreferencesRepository.save(defaultPreferences);
    }

    public UserPreferencesResponse updateUserPreferences(Long userId, UserPreferencesUpdateRequest request) {
        UserPreferences preferences = findUserPreferencesByUserId(userId);
        preferences.updatePreferences(
                request.dailyQuestionLimit(),
                request.questionMode(),
                request.answerType(),
                request.timeLimitSeconds(),
                request.allowPush()
        );

        return UserPreferencesResponse.from(preferences);
    }

    public void updateUserJob(Long userId, UserJobsUpdateRequest request) {
        UserPreferences preferences = findUserPreferencesByUserId(userId);
        Job job = jobRepository.findById(request.jobId())
                .orElseThrow(() -> new BusinessException(ErrorCode.JOB_NOT_FOUND));
        preferences.changeJob(job);
    }

    @Transactional(readOnly = true)
    public UserPreferences findUserPreferencesByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return userPreferencesRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
