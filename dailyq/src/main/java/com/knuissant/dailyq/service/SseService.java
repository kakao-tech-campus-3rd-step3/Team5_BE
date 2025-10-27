package com.knuissant.dailyq.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SseService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final Long TIMEOUT = 15 * 60 * 1000L;

    public SseEmitter connectSse(Long userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected"));
        } catch (IOException e) {
            log.error("Failed to send initial SSE connect event to user: {}", userId, e);
            emitter.completeWithError(e);
        }

        return emitter;
    }

}
