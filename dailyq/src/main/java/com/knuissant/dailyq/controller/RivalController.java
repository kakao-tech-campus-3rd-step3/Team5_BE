package com.knuissant.dailyq.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.dto.rivals.ReceivedRivalRequest;
import com.knuissant.dailyq.dto.rivals.RivalResponse;
import com.knuissant.dailyq.service.RivalService;

@RestController
@RequestMapping("/api/rivals")
@RequiredArgsConstructor
public class RivalController {

    private final RivalService rivalService;

    @PostMapping("/{targetUserId}")
    public ResponseEntity<RivalResponse> sendRivalRequest(
            @PathVariable Long targetUserId) {

        Long senderId = 1L; // 임시

        RivalResponse rivalResponseDto = rivalService.sendRivalRequest(senderId, targetUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(rivalResponseDto);
    }

    @GetMapping("/requests/received")
    public ResponseEntity<List<ReceivedRivalRequest>> getReceivedRequests() {

        Long receiverId = 2L; // 임시

        List<ReceivedRivalRequest> responses = rivalService.getReceivedRequests(receiverId);

        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/requests/accept/{senderId}")
    public ResponseEntity<RivalResponse> acceptRivalRequest(@PathVariable Long senderId) {

        Long receiverId = 2L; // 임시

        RivalResponse response = rivalService.acceptRivalRequest(senderId, receiverId);

        return ResponseEntity.ok(response);
    }
}
