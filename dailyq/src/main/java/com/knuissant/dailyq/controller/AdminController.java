package com.knuissant.dailyq.controller;

import java.util.List;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.dto.admin.JobManagementDto;
import com.knuissant.dailyq.dto.admin.OccupationManagementDto;
import com.knuissant.dailyq.dto.admin.QuestionManagementDto;
import com.knuissant.dailyq.dto.admin.UserManagementDto;
import com.knuissant.dailyq.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /* =========================
       User Management
       ========================= */

    @GetMapping("/users")
    public ResponseEntity<List<UserManagementDto.UserResponse>> getAllUsers() {
        List<UserManagementDto.UserResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserManagementDto.UserDetailResponse> getUserById(@PathVariable Long userId) {
        UserManagementDto.UserDetailResponse user = adminService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserManagementDto.UserResponse> updateUser(@PathVariable Long userId, @Valid @RequestBody UserManagementDto.UserUpdateRequest request) {
        UserManagementDto.UserResponse updatedUser = adminService.updateUser(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    /* =========================
       Occupation Management
       ========================= */

    @PostMapping("/occupations")
    public ResponseEntity<OccupationManagementDto.OccupationResponse> createOccupation(@Valid @RequestBody OccupationManagementDto.OccupationCreateRequest request) {
        OccupationManagementDto.OccupationResponse newOccupation = adminService.createOccupation(request);
        return new ResponseEntity<>(newOccupation, HttpStatus.CREATED);
    }

    @PutMapping("/occupations/{occupationId}")
    public ResponseEntity<OccupationManagementDto.OccupationResponse> updateOccupation(@PathVariable Long occupationId, @Valid @RequestBody OccupationManagementDto.OccupationUpdateRequest request) {
        OccupationManagementDto.OccupationResponse updatedOccupation = adminService.updateOccupation(occupationId, request);
        return ResponseEntity.ok(updatedOccupation);
    }

    @DeleteMapping("/occupations/{occupationId}")
    public ResponseEntity<Void> deleteOccupation(@PathVariable Long occupationId) {
        adminService.deleteOccupation(occupationId);
        return ResponseEntity.noContent().build();
    }

     /* =========================
       Job Management
       ========================= */

    @PostMapping("/jobs")
    public ResponseEntity<JobManagementDto.JobResponse> createJob(@Valid @RequestBody JobManagementDto.JobCreateRequest request) {
        JobManagementDto.JobResponse newJob = adminService.createJob(request);
        return new ResponseEntity<>(newJob, HttpStatus.CREATED);
    }

    @PutMapping("/jobs/{jobId}")
    public ResponseEntity<JobManagementDto.JobResponse> updateJob(@PathVariable Long jobId, @Valid @RequestBody JobManagementDto.JobUpdateRequest request) {
        JobManagementDto.JobResponse updatedJob = adminService.updateJob(jobId, request);
        return ResponseEntity.ok(updatedJob);
    }

    @DeleteMapping("/jobs/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId) {
        adminService.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }

    /* =========================
       Question Management
       ========================= */

    @GetMapping("/questions")
    public ResponseEntity<List<QuestionManagementDto.QuestionResponse>> getAllQuestions() {
        List<QuestionManagementDto.QuestionResponse> questions = adminService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/questions")
    public ResponseEntity<QuestionManagementDto.QuestionDetailResponse> createQuestion(@Valid @RequestBody QuestionManagementDto.QuestionCreateRequest request) {
        QuestionManagementDto.QuestionDetailResponse newQuestion = adminService.createQuestion(request);
        return new ResponseEntity<>(newQuestion, HttpStatus.CREATED);
    }

    @PutMapping("/questions/{questionId}")
    public ResponseEntity<QuestionManagementDto.QuestionDetailResponse> updateQuestion(@PathVariable Long questionId, @Valid @RequestBody QuestionManagementDto.QuestionUpdateRequest request) {
        QuestionManagementDto.QuestionDetailResponse updatedQuestion = adminService.updateQuestion(questionId, request);
        return ResponseEntity.ok(updatedQuestion);
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        adminService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
}