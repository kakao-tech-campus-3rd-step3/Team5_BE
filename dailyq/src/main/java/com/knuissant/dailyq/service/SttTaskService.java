package com.knuissant.dailyq.service;

import java.net.URI;
import java.net.URL;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.config.NcpConfig;
import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.stt.SttTask;
import com.knuissant.dailyq.domain.stt.SttTaskStatus;
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

        String token = requestStt(savedSttTask);
        savedSttTask.setToken(token);
    }

    @Transactional
    public void retrySttForAnswer(Long userId, Long answerId) {

        SttTask sttTask = sttTaskRepository.findByAnswerId(answerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STT_TASK_NOT_FOUND, "answerId:", answerId));

        if (sttTask.getStatus() == SttTaskStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.STT_TASK_ALREADY_COMPLETED, sttTask.getId());
        }

        Answer answer = sttTask.getAnswer();
        if (answer == null) {
            throw new BusinessException(ErrorCode.ANSWER_NOT_FOUND, "sttTaskId:", sttTask.getId());
        }
        answer.checkOwnership(userId);

        sttTask.retry();
        answer.retryStt();

        String newToken = requestStt(sttTask);
        sttTask.setToken(newToken);
    }

    private String requestStt(SttTask sttTask) {
        String dataKey = parseDataKeyFromUrl(sttTask.getAudioUrl());

        return clovaSpeechClient.requestTranscription(dataKey, sttTask.getId());
    }

    private String parseDataKeyFromUrl(String url) {
        try {
            URL parsedUrl = new URI(url).toURL();
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
