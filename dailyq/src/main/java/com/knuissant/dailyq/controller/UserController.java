package com.knuissant.dailyq.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.dto.users.UserJobsUpdateRequest;
import com.knuissant.dailyq.dto.users.UserPreferencesResponse;
import com.knuissant.dailyq.dto.users.UserPreferencesUpdateRequest;
import com.knuissant.dailyq.dto.users.UserProfileResponse;
import com.knuissant.dailyq.dto.users.UserUpdateRequest;
import com.knuissant.dailyq.service.UserPreferencesService;
import com.knuissant.dailyq.service.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserPreferencesService userPreferencesService;


    @GetMapping
    public ResponseEntity<UserProfileResponse> getUserProfile(@AuthenticationPrincipal User principal) {
        Long userId = Long.parseLong(principal.getUsername());
        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<UserProfileResponse> updateUserName(@AuthenticationPrincipal User principal, @RequestBody UserUpdateRequest updateRequest) {
        Long userId = Long.parseLong(principal.getUsername());
        userService.updateUserName(userId, updateRequest.name());
        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/preferences")
    public ResponseEntity<UserPreferencesResponse> updateUserPreferences(@AuthenticationPrincipal User principal, @RequestBody UserPreferencesUpdateRequest updateRequest) {
        Long userId = Long.parseLong(principal.getUsername());
        UserPreferencesResponse response = userPreferencesService.updateUserPreferences(userId, updateRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/jobs")
    public ResponseEntity<UserProfileResponse> updateUserJob(@AuthenticationPrincipal User principal, @RequestBody UserJobsUpdateRequest updateRequest) {
        Long userId = Long.parseLong(principal.getUsername());
        userPreferencesService.updateUserJob(userId, updateRequest);
        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }
}