package com.knuissant.dailyq.dto.rivals;

import com.knuissant.dailyq.domain.rivals.Rival;
import com.knuissant.dailyq.domain.rivals.RivalStatus;

public record RivalResponse (
        Long rivalId,
        Long senderId,
        String senderName,
        Long receiverId,
        String receiverName,
        RivalStatus status
) {

    public static RivalResponse from(Rival rival) {
        return new RivalResponse(
                rival.getId(),
                rival.getSender().getId(),
                rival.getSender().getName(),
                rival.getReceiver().getId(),
                rival.getReceiver().getName(),
                rival.getStatus()
        );
    }
}
