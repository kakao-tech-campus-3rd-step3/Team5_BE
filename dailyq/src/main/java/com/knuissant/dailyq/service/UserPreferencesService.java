package com.knuissant.dailyq.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.knuissant.dailyq.domain.jobs.Job;
import com.knuissant.dailyq.domain.questions.QuestionMode;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.domain.users.UserPreferences;
import com.knuissant.dailyq.domain.users.UserResponseType;
import com.knuissant.dailyq.dto.users.UserJobsUpdateRequest;
import com.knuissant.dailyq.dto.users.UserPreferencesResponse;
import com.knuissant.dailyq.dto.users.UserPreferencesUpdateRequest;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.exception.InfraException;
import com.knuissant.dailyq.repository.JobRepository;
import com.knuissant.dailyq.repository.UserPreferencesRepository;
import com.knuissant.dailyq.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserPreferencesService {

    private final UserPreferencesRepository userPreferencesRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    /**
     * 기존 사용자(로그인 시 UserPreferences가 생성되지 않은 사용자)를 위한 기본 preferences 생성
     * 이 메서드는 PUT /api/user/preferences에서 preferences가 없을 때 호출됩니다.
     */
    public UserPreferencesResponse createDefaultUserPreferences(Long userId) {
        // 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        // 이미 preferences가 존재하는지 확인
        if (userPreferencesRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_PREFERENCES_ALREADY_EXISTS);
        }
        
        // 기본 직업을 "백엔드 개발자" (jobId=1)로 설정
        Job defaultJob = jobRepository.findById(1L)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOB_NOT_FOUND, "기본 직업 정보를 찾을 수 없습니다."));
        
        UserPreferences defaultPreferences = UserPreferences.builder()
                .userId(userId)
                .user(user)
                .dailyQuestionLimit(1)
                .questionMode(QuestionMode.TECH)
                .userResponseType(UserResponseType.TEXT)
                .timeLimitSeconds(180)
                .allowPush(false)
                .userJob(defaultJob)
                .build();
        
        UserPreferences savedPreferences = userPreferencesRepository.save(defaultPreferences);
        return UserPreferencesResponse.from(savedPreferences);
    }

    public UserPreferencesResponse updateUserPreferences(Long userId, UserPreferencesUpdateRequest request) {
        UserPreferences preferences;
        
        try {
            preferences = findUserPreferencesByUserId(userId);
        } catch (BusinessException e) {
            if (e.getErrorCode() == ErrorCode.USER_PREFERENCES_NOT_FOUND) {
                // preferences가 없는 경우 기본값으로 생성
                createDefaultUserPreferences(userId);
                preferences = findUserPreferencesByUserId(userId);
            } else {
                throw e;
            }
        }
        
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
        return userPreferencesRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_PREFERENCES_NOT_FOUND));
    }
}