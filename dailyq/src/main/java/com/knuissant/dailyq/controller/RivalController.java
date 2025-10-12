package com.knuissant.dailyq.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
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
            @AuthenticationPrincipal User principal,
            @PathVariable Long targetUserId) {

        Long senderId = Long.parseLong(principal.getUsername());

        RivalResponse rivalResponseDto = rivalService.followRival(senderId, targetUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(rivalResponseDto);
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<Void> unfollowRival(
            @AuthenticationPrincipal User principal,
            @PathVariable Long targetUserId) {

        Long currentUserId = Long.parseLong(principal.getUsername());

        rivalService.unfollowRival(currentUserId, targetUserId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<RivalProfileResponse> getProfile(
            @AuthenticationPrincipal User principal,
            @PathVariable Long userId) {

        Long loginUserId = Long.parseLong(principal.getUsername());

        RivalProfileResponse response = rivalService.getProfile(userId, loginUserId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<RivalSearchResponse> searchRivalByEmail(@RequestParam String email) {

        RivalSearchResponse response = rivalService.searchRivalByEmail(email);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/following")
    public ResponseEntity<RivalListResponse.CursorResult> getFollowingRivalList(
            @AuthenticationPrincipal User principal,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "20") int limit) {

        Long userId = Long.parseLong(principal.getUsername());

        RivalListResponse.CursorResult response = rivalService.getFollowingRivalList(userId, lastId,
                limit);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/followed")
    public ResponseEntity<RivalListResponse.CursorResult> getFollowedRivalList(
            @AuthenticationPrincipal User principal,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "20") int limit) {

        Long userId = Long.parseLong(principal.getUsername());

        RivalListResponse.CursorResult response = rivalService.getFollowedRivalList(userId, lastId,
                limit);

        return ResponseEntity.ok(response);
    }
}
