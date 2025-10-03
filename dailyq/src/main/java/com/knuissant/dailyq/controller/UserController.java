package com.knuissant.dailyq.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.dto.users.UserJobsUpdateRequest;
import com.knuissant.dailyq.dto.users.UserPreferencesResponse;
import com.knuissant.dailyq.dto.users.UserPreferencesUpdateRequest;
import com.knuissant.dailyq.dto.users.UserProfileResponse;
import com.knuissant.dailyq.dto.users.UserUpdateRequest;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.exception.InfraException;
import com.knuissant.dailyq.service.UserPreferencesService;
import com.knuissant.dailyq.service.UserService;
import com.knuissant.dailyq.util.JwtUtils;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserPreferencesService userPreferencesService;
    private final JwtUtils jwtUtils;


    @GetMapping
    public ResponseEntity<UserProfileResponse> getUserProfile(HttpServletRequest request) {
        Long userId = jwtUtils.getUserIdFromRequest(request);
        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<UserProfileResponse> updateUserName(HttpServletRequest request, @RequestBody UserUpdateRequest updateRequest) {
        Long userId = jwtUtils.getUserIdFromRequest(request);
        userService.updateUserName(userId, updateRequest.name());
        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/preferences")
    public ResponseEntity<UserPreferencesResponse> updateUserPreferences(HttpServletRequest request, @RequestBody UserPreferencesUpdateRequest updateRequest) {
        Long userId = jwtUtils.getUserIdFromRequest(request);
        
        try {
            // 기존 preferences 업데이트 시도
            UserPreferencesResponse response = userPreferencesService.updateUserPreferences(userId, updateRequest);
            return ResponseEntity.ok(response);
        } catch (BusinessException e) {
            if (e.getErrorCode() == ErrorCode.USER_PREFERENCES_NOT_FOUND) {
                // preferences가 없는 경우 기본값으로 생성 후 업데이트
                userPreferencesService.createDefaultUserPreferences(userId);
                UserPreferencesResponse response = userPreferencesService.updateUserPreferences(userId, updateRequest);
                return ResponseEntity.ok(response);
            }
            throw new InfraException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 설정 업데이트 중 오류가 발생했습니다.");
        }
    }

    @PutMapping("/jobs")
    public ResponseEntity<UserProfileResponse> updateUserJob(HttpServletRequest request, @RequestBody UserJobsUpdateRequest updateRequest) {
        Long userId = jwtUtils.getUserIdFromRequest(request);
        userPreferencesService.updateUserJob(userId, updateRequest);
        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }
}