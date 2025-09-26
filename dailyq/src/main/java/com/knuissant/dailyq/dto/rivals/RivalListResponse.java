package com.knuissant.dailyq.dto.rivals;

import java.util.List;

import com.knuissant.dailyq.domain.users.User;

public record RivalListResponse(
        Long userId,
        String name,
        Integer streak
) {

    public record CursorResult(
            List<RivalListResponse> items,
            Long nextCursor,
            boolean hasNext
    ) {

    }

    public static RivalListResponse from(User user) {
        return new RivalListResponse(user.getId(), user.getName(), user.getStreak());
    }
}
