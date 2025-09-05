package com.knuissant.dailyq.Question.entity.vo;

public final class QuestionContent {
    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 2000;

    private final String value;

    private QuestionContent(String value) {
        this.value = value;
    }

    public static QuestionContent of(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("content must not be null");
        }
        String v = raw.trim();
        if (v.length() < MIN_LENGTH || v.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("content length must be between " + MIN_LENGTH + " and " + MAX_LENGTH);
        }
        return new QuestionContent(v);
    }

    public String getValue() {
        return value;
    }
}


