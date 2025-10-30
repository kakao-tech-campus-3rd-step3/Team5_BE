package com.knuissant.dailyq.service;

import org.springframework.util.StringUtils;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.answers.AnswerType;
import com.knuissant.dailyq.domain.questions.FollowUpQuestion;
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.dto.answers.AnswerCreateRequest;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.AnswerRepository;

public class FollowUpAnswerHandler extends AbstractAnswerHandler {

    private final FollowUpQuestionService followUpQuestionService;

    private FollowUpQuestion followUpQuestion;

    public FollowUpAnswerHandler(AnswerRepository answerRepository, SttTaskService sttTaskService,
            FollowUpQuestionService followUpQuestionService, User user, AnswerCreateRequest request) {
        super(answerRepository, sttTaskService, user, request);
        this.followUpQuestionService = followUpQuestionService;
    }

    @Override
    protected Question getQuestion() {
        // 꼬리 질문 조회와 권한 검증
        Long followUpQuestionId = Math.abs(answerCreateRequest.questionId());
        this.followUpQuestion = followUpQuestionService.getFollowUpQuestion(followUpQuestionId);

        if (!this.followUpQuestion.getAnswer().getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, "userId:", user.getId(), "followUpQuestionId:", followUpQuestionId);
        }
        // 꼬리질문의 원본질문에 답변이 달리도록 구현
        return this.followUpQuestion.getAnswer().getQuestion();
    }

    @Override
    protected void preSave(Answer answer) {
        // answer 객체에 FollowUpQuestion 연결 필요
        answer.setFollowUpQuestion(this.followUpQuestion);
    }

    @Override
    protected void postSave(Answer savedAnswer) {

        // 꼬리 질문 상태 변경 - 답변 완료
        followUpQuestionService.markFollowUpQuestionAsAnswered(this.followUpQuestion.getId());

        // 음성답변일시 - STT 요청
        if (savedAnswer.getAnswerType() == AnswerType.VOICE && StringUtils.hasText(answerCreateRequest.audioUrl())) {
            sttTaskService.createAndRequestSttTask(savedAnswer, answerCreateRequest.audioUrl());
        }
    }
}
