package com.knuissant.dailyq.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.domain.users.UserPreferences;
import com.knuissant.dailyq.dto.users.UserProfileResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.UserRepository;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserPreferencesService userPreferencesService;
    private final FollowUpQuestionService followUpQuestionService;

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        User user = findUserById(userId);
        UserPreferences preferences = userPreferencesService.findUserPreferencesByUserId(userId);
        long unansweredFollowUpQuestionCount = followUpQuestionService.countUnansweredFollowUpQuestions(userId);

        return UserProfileResponse.from(user, preferences, unansweredFollowUpQuestionCount);
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
