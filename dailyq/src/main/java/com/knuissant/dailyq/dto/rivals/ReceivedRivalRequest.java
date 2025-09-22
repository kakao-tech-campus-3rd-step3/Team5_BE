package com.knuissant.dailyq.dto.rivals;

import com.knuissant.dailyq.domain.rivals.Rival;

public record ReceivedRivalRequest(
        Long rivalId,
        Long requesterId,
        String requesterName
) {
    public static ReceivedRivalRequest from(Rival rival) {
        return new ReceivedRivalRequest(
                rival.getId(),
                rival.getSender().getId(),
                rival.getSender().getName()
        );
    }
}
