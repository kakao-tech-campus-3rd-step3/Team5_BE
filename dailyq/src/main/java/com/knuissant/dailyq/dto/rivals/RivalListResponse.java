package com.knuissant.dailyq.dto.rivals;

import java.util.List;

public record RivalListResponse(
        Long userId,
        String name,
        String email
) {

    public record CursorResult(
            List<RivalListResponse> items,
            Long nextCursor,
            boolean hasNext
    ) {

        public static CursorResult from(List<RivalListResponse> items, Long nextCursor,
                boolean hasNext) {
            return new CursorResult(items, nextCursor, hasNext);
        }
    }

    public static RivalListResponse from(Long userId, String name, String email) {
        return new RivalListResponse(userId, name, email);
    }
}
