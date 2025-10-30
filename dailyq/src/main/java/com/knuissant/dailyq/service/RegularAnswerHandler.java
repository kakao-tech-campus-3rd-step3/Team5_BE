package com.knuissant.dailyq.service;

import org.springframework.util.StringUtils;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.answers.AnswerType;
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.dto.answers.AnswerCreateRequest;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.AnswerRepository;
import com.knuissant.dailyq.repository.QuestionRepository;

public class RegularAnswerHandler extends AbstractAnswerHandler {

    private final QuestionRepository questionRepository;

    public RegularAnswerHandler(AnswerRepository answerRepository, SttTaskService sttTaskService,
            QuestionRepository questionRepository, User user, AnswerCreateRequest request) {
        super(answerRepository, sttTaskService, user, request);
        this.questionRepository = questionRepository;
    }

    @Override
    protected Question getQuestion() {
        // 일반 질문 조회
        return questionRepository.findById(answerCreateRequest.questionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND,
                        answerCreateRequest.questionId()));
    }

    // preSave() 작업 필요 X

    @Override
    protected void postSave(Answer savedAnswer) {
        // 음성 답변인 경우 STT 요청
        if (savedAnswer.getAnswerType() == AnswerType.VOICE && StringUtils.hasText(
                answerCreateRequest.audioUrl())) {
            sttTaskService.createAndRequestSttTask(savedAnswer, answerCreateRequest.audioUrl());
        }
    }
}
