package com.knuissant.dailyq.dto.rivals;

import java.time.LocalDate;
import java.util.List;

import com.knuissant.dailyq.domain.users.User;

public record RivalProfileResponse(
        String name,
        Integer streak,
        Long totalAnswerCount,
        List<DailySolveCount> dailySolveCounts,
        boolean isMe
) {

    public record DailySolveCount(
            LocalDate date,
            Long count
    ) {

    }

    public static RivalProfileResponse from(User user, long totalAnswerCount,
            List<DailySolveCount> dailySolveCounts, boolean isMe) {
        return new RivalProfileResponse(
                user.getName(),
                user.getStreak(),
                totalAnswerCount,
                dailySolveCounts,
                isMe
        );
    }
}
