package com.knuissant.dailyq.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.external.ncp.clova.ClovaCallbackPayload;
import com.knuissant.dailyq.service.SttCallbackService;

@RestController
@RequestMapping("/api/stt/callback")
@RequiredArgsConstructor
public class SttCallbackController {

    private final SttCallbackService sttCallbackService;

    @PostMapping("/{sttTaskId}")
    public ResponseEntity<Void> handleClovaCallback(
            @PathVariable Long sttTaskId,
            @Valid @RequestBody ClovaCallbackPayload payload) {

        sttCallbackService.processCallback(sttTaskId, payload);

        return ResponseEntity.ok().build();
    }
}
