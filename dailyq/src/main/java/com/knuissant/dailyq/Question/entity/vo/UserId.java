package com.knuissant.dailyq.Question.entity.vo;

public final class UserId {
    private final long value;

    private UserId(long value) {
        this.value = value;
    }

    public static UserId of(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("userId must be positive");
        }
        return new UserId(id);
    }

    public long getValue() {
        return value;
    }
}


