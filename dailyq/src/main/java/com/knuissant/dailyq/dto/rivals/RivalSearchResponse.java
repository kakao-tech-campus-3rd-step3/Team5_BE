package com.knuissant.dailyq.dto.rivals;

import com.knuissant.dailyq.domain.users.User;

public record RivalSearchResponse(
        Long userId,
        String email,
        String name
) {

    public static RivalSearchResponse from(User user) {
        return new RivalSearchResponse(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }
}
