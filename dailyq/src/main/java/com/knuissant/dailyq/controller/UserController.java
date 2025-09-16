package com.knuissant.dailyq.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<UserProfileResponse> getUserProfile(@RequestParam Long userId) {
        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<UserProfileResponse> updateUserName(@RequestParam Long userId, @RequestBody UserUpdateRequest request) {
        userService.updateUserName(userId, request.name());
        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/preferences")
    public ResponseEntity<UserPreferencesResponse> updateUserPreferences(@RequestParam Long userId, @RequestBody UserPreferencesUpdateRequest request) {
        UserPreferencesResponse response = userPreferencesService.updateUserPreferences(userId, request);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/jobs")
    public ResponseEntity<UserProfileResponse> updateUserJob(@RequestParam Long userId, @RequestBody UserJobsUpdateRequest request) {
        userPreferencesService.updateUserJob(userId, request);
        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }
}
