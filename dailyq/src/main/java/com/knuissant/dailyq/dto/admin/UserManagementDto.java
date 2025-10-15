package com.knuissant.dailyq.dto.admin;

import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.domain.users.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

public class UserManagementDto {

    // 회원 목록 조회 시 사용
    @Builder
    public record UserResponse(
            Long userId,
            String email,
            String name,
            UserRole role
    ) {
        public static UserResponse from(User user) {
            return new UserResponse(user.getId(), user.getEmail(), user.getName(), user.getRole());
        }
    }

    // 회원 상세 정보 조회 시 사용
    @Builder
    public record UserDetailResponse(
            Long userId,
            String email,
            String name,
            UserRole role,
            Integer streak,
            Boolean solvedToday,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static UserDetailResponse from(User user) {
            return new UserDetailResponse(user.getId(), user.getEmail(), user.getName(), user.getRole(),
                    user.getStreak(), user.getSolvedToday(), user.getCreatedAt(), user.getUpdatedAt());
        }
    }

    // 회원 정보 수정 시 사용
    public record UserUpdateRequest(
            @NotBlank(message = "이름은 필수입니다.")
            String name,
            @NotNull(message = "역할은 필수입니다.")
            UserRole role
    ) {
    }
}