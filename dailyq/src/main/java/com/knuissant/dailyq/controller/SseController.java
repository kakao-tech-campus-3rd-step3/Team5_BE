package com.knuissant.dailyq.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.dto.sse.SseTokenResponse;
import com.knuissant.dailyq.service.SseService;
import com.knuissant.dailyq.service.TokenService;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;
    private final TokenService tokenService;

    @GetMapping("/token")
    public ResponseEntity<SseTokenResponse> getSseToken(@AuthenticationPrincipal User principal) {
        Long userId = Long.parseLong(principal.getUsername());

        return ResponseEntity.ok(new SseTokenResponse(tokenService.generateSseToken(userId)));
    }


    @GetMapping(path = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connectSse(@AuthenticationPrincipal User principal) {
        Long userId = Long.parseLong(principal.getUsername());

        return ResponseEntity.ok(sseService.connectSse(userId));
    }

}
