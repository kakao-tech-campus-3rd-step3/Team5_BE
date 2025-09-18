package com.knuissant.dailyq.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

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
}
