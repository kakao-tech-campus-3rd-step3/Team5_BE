package com.knuissant.dailyq.dto.rivals;

import java.time.LocalDate;
import java.util.List;

import com.knuissant.dailyq.domain.users.User;

public record RivalProfileResponse(
        String name,
        Integer streak,
        Long totalAnswerCount,
        List<LocalDate> datesForStreak
) {

    public static RivalProfileResponse from(User user, long totalAnswerCount,
            List<LocalDate> datesForStreak) {
        return new RivalProfileResponse(
                user.getName(),
                user.getStreak(),
                totalAnswerCount,
                datesForStreak
        );
    }
}
