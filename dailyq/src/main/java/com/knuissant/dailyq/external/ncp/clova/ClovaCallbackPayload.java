package com.knuissant.dailyq.external.ncp.clova;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClovaCallbackPayload(
        @NotBlank
        String result,  // 응답 코드
        String message, // 응답 메시지
        @NotBlank
        String token,   // 결과 토큰
        String text     // 전체 텍스트
) {

    public boolean isComplete() {
        return "SUCCEEDED".equalsIgnoreCase(result);
    }

}
