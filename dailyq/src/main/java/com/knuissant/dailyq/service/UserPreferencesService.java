package com.knuissant.dailyq.service;

import com.knuissant.dailyq.domain.jobs.Job;
import com.knuissant.dailyq.domain.questions.QuestionMode;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.domain.users.UserPreferences;
import com.knuissant.dailyq.domain.users.UserResponseType;
import com.knuissant.dailyq.dto.UserJobsUpdateRequest;
import com.knuissant.dailyq.dto.UserPreferencesUpdateRequest;
import com.knuissant.dailyq.repository.JobRepository;
import com.knuissant.dailyq.repository.UserPreferencesRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
                .orElseThrow(() -> new EntityNotFoundException("Default Job with ID 1 not found."));

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
     */
    public void updateUserPreferences(Long userId, UserPreferencesUpdateRequest request) {
        UserPreferences preferences = findUserPreferencesByUserId(userId);
        preferences.updatePreferences(request);
    }

    /**
     * Updates a user's representative job.
     * @param userId The ID of the user
     * @param request The job update request DTO
     */
    public void updateUserJob(Long userId, UserJobsUpdateRequest request) {
        if (CollectionUtils.isEmpty(request.jobIds())) {
            return;
        }

        UserPreferences preferences = findUserPreferencesByUserId(userId);
        Long representativeJobId = request.jobIds().get(0);
        Job job = jobRepository.findById(representativeJobId)
                .orElseThrow(() -> new EntityNotFoundException("Job not found with id: " + representativeJobId));

        preferences.changeJob(job);
    }

    public UserPreferences findUserPreferencesByUserId(Long userId) {
        return userPreferencesRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("UserPreferences not found for user id: " + userId));
    }
}

