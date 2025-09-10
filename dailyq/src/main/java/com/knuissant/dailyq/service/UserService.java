package com.knuissant.dailyq.service;

import com.knuissant.dailyq.domain.jobs.Job;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.domain.users.UserPreferences;
import com.knuissant.dailyq.domain.users.UserRole;
import com.knuissant.dailyq.dto.UserCreateRequest;
import com.knuissant.dailyq.dto.UserProfileResponse;
import com.knuissant.dailyq.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserPreferencesService userPreferencesService;

    /**
     * 신규 사용자를 생성하고 기본 환경설정을 구성한 뒤, 프로필 정보를 반환합니다.
     * @param request 사용자 생성 요청 DTO
     * @return 생성된 사용자의 프로필 정보
     */
    public UserProfileResponse createUserAndGetProfile(UserCreateRequest request) {
        User newUser = User.builder()
                .email(request.email())
                .name(request.name())
                .role(UserRole.FREE)
                .streak(0)
                .solvedToday(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(newUser);

        // UserPreferencesService를 통해 기본 환경설정 생성
        userPreferencesService.createDefaultPreferences(savedUser);

        return getUserProfile(savedUser.getId());
    }

    /**
     * 특정 사용자의 전체 프로필 정보를 조회합니다.
     * @param userId 조회할 사용자 ID
     * @return 사용자 프로필 정보
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        User user = findUserById(userId);
        UserPreferences preferences = userPreferencesService.findUserPreferencesByUserId(userId);

        List<UserProfileResponse.JobDto> jobDtos = Collections.emptyList();
        if (preferences.getUserJob() != null) {
            Job userJob = preferences.getUserJob();
            jobDtos = List.of(new UserProfileResponse.JobDto(userJob.getId(), userJob.getName()));
        }

        UserProfileResponse.PreferencesDto preferencesDto = new UserProfileResponse.PreferencesDto(
                preferences.getDailyQuestionLimit(),
                preferences.getQuestionMode(),
                preferences.getUserResponseType(),
                preferences.getTimeLimitSeconds(),
                preferences.getAllowPush()
        );

        return UserProfileResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .streak(user.getStreak())
                .solvedToday(user.getSolvedToday())
                .preferences(preferencesDto)
                .jobs(jobDtos)
                .build();
    }

    /**
     * 사용자 이름을 수정합니다.
     * @param userId 사용자 ID
     * @param newName 변경할 새 이름
     */
    public void updateUserName(Long userId, String newName) {
        User user = findUserById(userId);
        // User 엔티티에 이름 변경을 위한 메서드 (예: user.updateName(newName)) 호출 또는 Setter 사용
        // user.setName(newName); -> User 엔티티에 Setter가 필요합니다.
        // 아래는 User 엔티티에 updateName 메서드가 있다는 가정 하에 작성되었습니다.
        // user.updateName(newName);
        userRepository.save(user);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }
}

