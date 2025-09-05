package com.knuissant.dailyq.Question.enums;

public enum FlowPhase {
    INTRO,
    MOTIVATION,
    TECH1,
    TECH2,
    PERSONALITY;

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


