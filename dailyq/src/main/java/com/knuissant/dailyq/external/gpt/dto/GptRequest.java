package com.knuissant.dailyq.external.gpt.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GptRequest(
        String model,
        List<Message> messages,
        @JsonProperty("response_format")
        ResponseFormat responseFormat
) {

    public record Message(
            String role,
            String content
    ) {

    }

    public record ResponseFormat(
            String type
    ) {

    }

}
