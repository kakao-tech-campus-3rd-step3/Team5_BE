package com.knuissant.dailyq.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.stt.SttTask;
import com.knuissant.dailyq.domain.stt.SttTaskStatus;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.external.ncp.clova.ClovaCallbackPayload;
import com.knuissant.dailyq.repository.SttTaskRepository;

@Service
@RequiredArgsConstructor
public class SttCallbackService {

    private final SttTaskRepository sttTaskRepository;

    @Transactional
    public void processCallback(Long sttTaskId, ClovaCallbackPayload payload) {
        SttTask sttTask = sttTaskRepository.findById(sttTaskId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STT_TASK_NOT_FOUND, sttTaskId));

        // 중복 처리 방지
        if (sttTask.getStatus() != SttTaskStatus.PENDING) {
            return;
        }

        Answer answer = sttTask.getAnswer();
        if (answer == null) {
            sttTask.fail("연결된 Answer 없음");
            throw new BusinessException(ErrorCode.ANSWER_NOT_FOUND, "sttTaskId:", sttTaskId);
        }

        if (payload.isComplete()) {
            String transcribedText = payload.text();
            sttTask.complete();
            answer.completeStt(transcribedText);

            // 피드백 생성 이벤트 발행 예정

        } else {
            String errorMessage = (payload.message() != null) ? payload.message() : "Unknown CLOVA error";
            sttTask.fail(errorMessage);
            answer.failStt();
        }
    }

}
