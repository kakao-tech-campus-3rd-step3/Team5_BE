package com.knuissant.dailyq.dto.rivals;

import java.time.LocalDate;
import java.util.List;

public record RivalProfileResponse(
        String name,
        int streak,
        long totalAnswerCount,
        List<LocalDate> datesForStreak
) {

}
