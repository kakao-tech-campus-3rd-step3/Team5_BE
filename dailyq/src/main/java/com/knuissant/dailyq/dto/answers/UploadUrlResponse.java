package com.knuissant.dailyq.dto.answers;

public record UploadUrlResponse(
        String preSignedUrl,
        String finalAudioUrl
) {

    public static UploadUrlResponse of(String preSignedUrl, String finalAudioUrl) {
        return new UploadUrlResponse(preSignedUrl, finalAudioUrl);
    }
}
