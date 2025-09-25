package com.knuissant.dailyq.dto.rivals;

import com.knuissant.dailyq.domain.rivals.Rival;

public record RivalResponse (
        Long rivalId,
        Long senderId,
        String senderName,
        Long receiverId,
        String receiverName
) {

    public static RivalResponse from(Rival rival) {
        return new RivalResponse(
                rival.getId(),
                rival.getSender().getId(),
                rival.getSender().getName(),
                rival.getReceiver().getId(),
                rival.getReceiver().getName()
        );
    }
}
