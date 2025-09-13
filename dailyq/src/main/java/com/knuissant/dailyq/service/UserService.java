package com.knuissant.dailyq.service;

import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.domain.users.UserPreferences;
import com.knuissant.dailyq.domain.users.UserRole;
import com.knuissant.dailyq.dto.UserCreateRequest;
import com.knuissant.dailyq.dto.UserProfileResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserPreferencesService userPreferencesService;

    public UserProfileResponse createUserAndGetProfile(UserCreateRequest request) {
        User newUser = User.builder()
                .email(request.email())
                .name(request.name())
                .role(UserRole.FREE)
                .streak(0)
                .solvedToday(false)
                .build();
        User savedUser = userRepository.save(newUser);

        userPreferencesService.createDefaultPreferences(savedUser);

        return getUserProfile(savedUser.getId());
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        User user = findUserById(userId);
        UserPreferences preferences = userPreferencesService.findUserPreferencesByUserId(userId);
        return UserProfileResponse.from(user, preferences);
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

