package com.knuissant.dailyq.service.handler;

import org.springframework.stereotype.Component;

import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.dto.answers.AnswerCreateRequest;
import com.knuissant.dailyq.repository.AnswerRepository;
import com.knuissant.dailyq.repository.QuestionRepository;
import com.knuissant.dailyq.service.FollowUpQuestionService;
import com.knuissant.dailyq.service.SttTaskService;


@Component
public class AnswerHandlerFactory {

    private final AnswerRepository answerRepository;
    private final SttTaskService sttTaskService;
    private final FollowUpQuestionService followUpQuestionService;
    private final QuestionRepository questionRepository;

    public AnswerHandlerFactory(AnswerRepository answerRepository, SttTaskService sttTaskService,
            FollowUpQuestionService followUpQuestionService, QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.sttTaskService = sttTaskService;
        this.followUpQuestionService = followUpQuestionService;
        this.questionRepository = questionRepository;
    }

    public AbstractAnswerHandler createHandler(User user, AnswerCreateRequest request, boolean isFollowUp) {
        if (isFollowUp) {
            return new FollowUpAnswerHandler(answerRepository, sttTaskService,
                    followUpQuestionService, user, request);
        }
        return new RegularAnswerHandler(answerRepository, sttTaskService,
                questionRepository, user, request);
    }
}
