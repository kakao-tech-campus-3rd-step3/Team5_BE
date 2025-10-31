package com.knuissant.dailyq.service;

import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.answers.AnswerType;
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.dto.answers.AnswerCreateRequest;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.AnswerRepository;

@RequiredArgsConstructor
public abstract class AbstractAnswerHandler {

    protected final AnswerRepository answerRepository;
    protected final SttTaskService sttTaskService;

    protected final User user;
    protected final AnswerCreateRequest answerCreateRequest;

    protected abstract Question getQuestion();

    protected void preSave(Answer answer) {
    }

    protected abstract void postSave(Answer savedAnswer);


    public final Answer handle() {
        // 질문 준비 - 달라진다
        Question question = getQuestion();

        // 답변 객체 생성 - 공통 작업
        Answer answer = createAnswerObject(question);

        // 저장 전 처리 - 달라진다 (Hook)
        preSave(answer);

        // 답변 저장 - 공통 작업
        Answer savedAnswer = answerRepository.save(answer);

        // 저장 후 처리- 달라진다
        postSave(savedAnswer);

        return savedAnswer;
    }

    protected void triggerSttIfNeeded(Answer savedAnswer) {
        if (savedAnswer.getAnswerType() == AnswerType.VOICE
                && StringUtils.hasText(answerCreateRequest.audioUrl())) {
            sttTaskService.createAndRequestSttTask(savedAnswer, answerCreateRequest.audioUrl());
        }
    }

    private Answer createAnswerObject(Question question) {
        if (StringUtils.hasText(answerCreateRequest.answerText())) {
            return Answer.createTextAnswer(user, question, answerCreateRequest.answerText());
        } else if (StringUtils.hasText(answerCreateRequest.audioUrl())) {
            return Answer.createVoiceAnswer(user, question);
        } else {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                    "answerText 또는 audioUrl이 필요합니다.");
        }
    }
}
