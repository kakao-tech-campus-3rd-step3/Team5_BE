package com.knuissant.dailyq.Question.entity.vo;

public final class DailyCount {
    public static final int MIN = 1;
    public static final int MAX = 20;

    private final int value;

    private DailyCount(int value) {
        this.value = value;
    }

    public static DailyCount of(Integer countOrNull) {
        int v = (countOrNull == null) ? MIN : countOrNull;
        if (v < MIN || v > MAX) {
            throw new IllegalArgumentException("count must be between " + MIN + " and " + MAX);
        }
        return new DailyCount(v);
    }

    public int getValue() {
        return value;
    }
}


