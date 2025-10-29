package com.knuissant.dailyq.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.stt.SttTask;
import com.knuissant.dailyq.domain.stt.SttTaskStatus;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.event.payload.SttCompletedEvent;
import com.knuissant.dailyq.event.payload.SttFailedEvent;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.external.ncp.clova.ClovaCallbackPayload;
import com.knuissant.dailyq.repository.SttTaskRepository;

@Service
@RequiredArgsConstructor
public class SttCallbackService {

    private final SttTaskRepository sttTaskRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void processCallback(Long sttTaskId, ClovaCallbackPayload payload) {
        SttTask sttTask = sttTaskRepository.findById(sttTaskId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STT_TASK_NOT_FOUND, sttTaskId));

        if (!payload.token().equals(sttTask.getToken())) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "sttTaskId:", sttTaskId);
        }

        if (sttTask.getStatus() != SttTaskStatus.PENDING) {
            return;
        }

        Answer answer = sttTask.getAnswer();
        if (answer == null) {
            sttTask.fail("연결된 Answer 없음");
            throw new BusinessException(ErrorCode.ANSWER_NOT_FOUND, "sttTaskId:", sttTaskId);
        }

        User user = answer.getUser();
        if (user == null) {
            sttTask.fail("연결된 User 없음");
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "answerId:", answer.getId());
        }

        if (payload.isComplete()) {
            String transcribedText = payload.text();
            sttTask.complete();
            answer.completeStt(transcribedText);

            // stt 변환 성공 이벤트 발행
            publisher.publishEvent(new SttCompletedEvent(user.getId(), answer.getId(), transcribedText));

            // 피드백 생성 이벤트 발행 예정

        } else {
            String errorMessage = (payload.message() != null) ? payload.message() : "Unknown CLOVA error";
            sttTask.fail(errorMessage);
            answer.failStt();

            // stt 변환 실패 이벤트 발행
            publisher.publishEvent(new SttFailedEvent(user.getId(), answer.getId()));
        }
    }

}
