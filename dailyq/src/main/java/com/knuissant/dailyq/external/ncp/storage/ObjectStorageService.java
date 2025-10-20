package com.knuissant.dailyq.external.ncp.storage;

import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import com.knuissant.dailyq.config.NcpConfig;
import com.knuissant.dailyq.dto.answers.UploadUrlResponse;

@Service
@RequiredArgsConstructor
public class ObjectStorageService {

    private final AmazonS3 ncpObjectStorageClient;
    private final NcpConfig ncpConfig;

    private static final long PRE_SIGNED_URL_EXPIRATION_MS = 1000 * 60 * 10;

    /**
     * Pre-signed URL과 최종 파일 URL을 생성
     *
     * @param fileName 원본 파일 이름
     * @return UploadUrlResponse (preSignedUrl, finalAudioUrl)
     */
    public UploadUrlResponse generateUploadUrl(String fileName) {
        String bucketName = ncpConfig.getBucketName();
        String objectKey = createObjectKey(fileName);
        Date expiration = new Date(Instant.now().toEpochMilli() + PRE_SIGNED_URL_EXPIRATION_MS);

        GeneratePresignedUrlRequest request =
                new GeneratePresignedUrlRequest(bucketName, objectKey)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration);

        URL preSignedUrl = ncpObjectStorageClient.generatePresignedUrl(request);

        String storageEndpoint = ncpConfig.getStorageEndpoint();
        String finalAudioUrl = storageEndpoint + "/" + bucketName + "/" + objectKey;

        return UploadUrlResponse.of(preSignedUrl.toString(), finalAudioUrl);
    }

    private String createObjectKey(String fileName) {
        String extension = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = fileName.substring(lastDotIndex);
        }
        return UUID.randomUUID() + extension;
    }
}
