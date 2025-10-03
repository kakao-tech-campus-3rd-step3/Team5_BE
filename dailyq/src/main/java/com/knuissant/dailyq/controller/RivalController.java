package com.knuissant.dailyq.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.dto.rivals.RivalListResponse;
import com.knuissant.dailyq.dto.rivals.RivalProfileResponse;
import com.knuissant.dailyq.dto.rivals.RivalResponse;
import com.knuissant.dailyq.dto.rivals.RivalSearchResponse;
import com.knuissant.dailyq.service.RivalService;

@RestController
@RequestMapping("/api/rivals")
@RequiredArgsConstructor
public class RivalController {

    private final RivalService rivalService;

    @PostMapping("/{targetUserId}")
    public ResponseEntity<RivalResponse> followRival(
            @PathVariable Long targetUserId) {

        Long senderId = 1L; // 임시

        RivalResponse rivalResponseDto = rivalService.followRival(senderId, targetUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(rivalResponseDto);
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<Void> unfollowRival(@PathVariable Long targetUserId) {

        Long currentUserId = 1L; // 임시

        rivalService.unfollowRival(currentUserId, targetUserId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<RivalProfileResponse> getProfile(@PathVariable Long userId) {

        RivalProfileResponse response = rivalService.getProfile(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<RivalSearchResponse> searchRivalByEmail(@RequestParam String email) {

        RivalSearchResponse response = rivalService.searchRivalByEmail(email);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/following")
    public ResponseEntity<RivalListResponse.CursorResult> getFollowingRivalList(
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "20") int limit) {

        Long userId = 1L; // 임시

        RivalListResponse.CursorResult response = rivalService.getFollowingRivalList(userId, lastId,
                limit);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/followed")
    public ResponseEntity<RivalListResponse.CursorResult> getFollowedRivalList(
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "20") int limit) {
        Long userId = 1L;// 임시

        RivalListResponse.CursorResult response = rivalService.getFollowedRivalList(userId, lastId,
                limit);

        return ResponseEntity.ok(response);
    }
}
