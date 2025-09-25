package com.knuissant.dailyq.dto.rivals;

import com.knuissant.dailyq.domain.users.User;

public record RivalListResponse(
        Long userId,
        String name,
        Integer streak
) {
    public static RivalListResponse from (User user) {
       return new RivalListResponse(user.getId(), user.getName(), user.getStreak());
    }
}
