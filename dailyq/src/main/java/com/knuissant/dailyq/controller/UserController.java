package com.knuissant.dailyq.controller;

import com.knuissant.dailyq.dto.*;
import com.knuissant.dailyq.service.UserPreferencesService;
import com.knuissant.dailyq.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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