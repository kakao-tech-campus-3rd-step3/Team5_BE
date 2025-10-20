package com.knuissant.dailyq.service;

import java.net.URL;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.config.NcpConfig;
import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.stt.SttTask;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.external.ncp.clova.ClovaSpeechClient;
import com.knuissant.dailyq.repository.SttTaskRepository;

@Service
@RequiredArgsConstructor
public class SttTaskService {

    private final SttTaskRepository sttTaskRepository;
    private final ClovaSpeechClient clovaSpeechClient;
    private final NcpConfig ncpConfig;

    /**
     * SttTask를 생성하고 Clova API에 STT 변환 요청
     *
     * @param savedAnswer   PENDING_STT 상태로 저장된 Answer
     * @param finalAudioUrl NCP Object Storage에 저장된 오디오 파일의 URL
     */
    @Transactional
    public void createAndRequestSttTask(Answer savedAnswer, String finalAudioUrl) {
        SttTask sttTask = SttTask.create(savedAnswer, finalAudioUrl);
        SttTask savedSttTask = sttTaskRepository.save(sttTask);

        String dataKey = parseDataKeyFromUrl(finalAudioUrl);
        clovaSpeechClient.requestTranscription(dataKey, savedSttTask.getId());
    }

    private String parseDataKeyFromUrl(String url) {
        try {
            URL parsedUrl = new URL(url);
            String path = parsedUrl.getPath();
            String bucketName = ncpConfig.getBucketName();
            String prefix = "/" + bucketName + "/";

            if (path.startsWith(prefix)) {
                return path.substring(prefix.length());
            } else {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Invalid audio URL: " + url);
        }
    }
}
