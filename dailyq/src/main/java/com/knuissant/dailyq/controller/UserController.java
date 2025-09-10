package com.knuissant.dailyq.controller;

import com.knuissant.dailyq.dto.UserCreateRequest;
import com.knuissant.dailyq.dto.UserJobsUpdateRequest;
import com.knuissant.dailyq.dto.UserPreferencesUpdateRequest;
import com.knuissant.dailyq.dto.UserProfileResponse;
import com.knuissant.dailyq.dto.UserUpdateRequest;
import com.knuissant.dailyq.service.UserPreferencesService;
import com.knuissant.dailyq.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserPreferencesService userPreferencesService;

    @PostMapping
    public ResponseEntity<UserProfileResponse> createUser(@RequestBody UserCreateRequest request) {
        UserProfileResponse response = userService.createUserAndGetProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long userId) {
        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Void> updateUserName(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        userService.updateUserName(userId, request.name());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/preferences")
    public ResponseEntity<Void> updateUserPreferences(@PathVariable Long userId, @RequestBody UserPreferencesUpdateRequest request) {
        userPreferencesService.updateUserPreferences(userId, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/jobs")
    public ResponseEntity<Void> updateUserJob(@PathVariable Long userId, @RequestBody UserJobsUpdateRequest request) {
        userPreferencesService.updateUserJob(userId, request);
        return ResponseEntity.noContent().build();
    }
}
