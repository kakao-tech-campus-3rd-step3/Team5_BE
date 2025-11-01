package com.knuissant.dailyq.domain.questions;

public enum FlowPhase {
    INTRO,
    MOTIVATION,
    TECH1,
    TECH2,
    PERSONALITY;

    /**
     * 현재 phase의 다음 phase를 반환합니다.
     */
    public FlowPhase next() {
        return switch (this) {
            case INTRO -> MOTIVATION;
            case MOTIVATION -> TECH1;
            case TECH1 -> TECH2;
            case TECH2 -> PERSONALITY;
            case PERSONALITY -> INTRO;
        };
    }
}


