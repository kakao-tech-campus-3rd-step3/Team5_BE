package com.knuissant.dailyq.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.domain.users.UserPreferences;
import com.knuissant.dailyq.dto.users.UserProfileResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.AnswerRepository;
import com.knuissant.dailyq.repository.UserRepository;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserPreferencesService userPreferencesService;
    private final FollowUpQuestionService followUpQuestionService;
    private final AnswerRepository answerRepository;

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        User user = findUserById(userId);
        UserPreferences preferences = userPreferencesService.findUserPreferencesByUserId(userId);
        long unansweredFollowUpQuestionCount = followUpQuestionService.countUnansweredFollowUpQuestions(userId);
        
        // 오늘 남은 일반 질문 개수 계산 (꼬리질문 제외)
        int remainingQuestionCount = calculateRemainingQuestionCount(userId, preferences);

        return UserProfileResponse.from(user, preferences, unansweredFollowUpQuestionCount, remainingQuestionCount);
    }

    /**
     * 오늘 남은 일반 질문 개수를 계산합니다.
     * 꼬리질문은 일일 제한에 포함되지 않으므로 제외합니다.
     */
    private int calculateRemainingQuestionCount(Long userId, UserPreferences preferences) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        
        // 오늘 답변한 일반 질문 개수 (꼬리질문 제외)
        long answeredToday = answerRepository.countByUserIdAndFollowUpQuestionIsNullAndCreatedAtBetween(
                userId, startOfDay, endOfDay);
        
        // 남은 질문 개수 = 일일 한도 - 오늘 답변한 개수 (최소 0)
        int remaining = preferences.getDailyQuestionLimit() - (int) answeredToday;
        return Math.max(0, remaining);
    }

    public void updateUserName(Long userId, String newName) {
        User user = findUserById(userId);
        user.updateName(newName);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));
    }
}
