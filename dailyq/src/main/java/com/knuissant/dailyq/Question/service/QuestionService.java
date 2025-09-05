package com.knuissant.dailyq.Question.service;

import com.knuissant.dailyq.Question.dto.DailyQuestionRequest;
import com.knuissant.dailyq.Question.dto.DailyQuestionResponse;
import com.knuissant.dailyq.Question.dto.QuestionResponse;
import com.knuissant.dailyq.Question.entity.Question;
import com.knuissant.dailyq.Question.entity.UserFlowProgress;
import com.knuissant.dailyq.Question.enums.FlowPhase;
import com.knuissant.dailyq.Question.enums.Mode;
import com.knuissant.dailyq.Question.repository.QuestionRepository;
import com.knuissant.dailyq.Question.repository.UserFlowProgressRepository;
import com.knuissant.dailyq.Question.entity.vo.DailyCount;
import com.knuissant.dailyq.Question.entity.vo.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserFlowProgressRepository userFlowProgressRepository;

    public QuestionService(QuestionRepository questionRepository,
                           UserFlowProgressRepository userFlowProgressRepository) {
        this.questionRepository = questionRepository;
        this.userFlowProgressRepository = userFlowProgressRepository;
    }

    public Optional<DailyQuestionResponse> getDailyQuestions(DailyQuestionRequest req) {
        Mode mode = req.getMode();
        DailyCount count = DailyCount.of(req.getCount());
        UserId userId = UserId.of(req.getUserId());

        if (mode == Mode.TECH) {
            return handleTech(req, count, userId);
        } else {
            return handleFlow(req, count, userId);
        }
    }

    private Optional<DailyQuestionResponse> handleTech(DailyQuestionRequest req, DailyCount count, UserId userId) {
        List<Question> list = questionRepository.findRandomTechExcludingSolved(
                req.getJobRole().name(), userId.getValue(), count.getValue());
        if (list.isEmpty()) return Optional.empty();
        return Optional.of(new DailyQuestionResponse(LocalDate.now(), req.getMode(), map(list)));
    }

    private Optional<DailyQuestionResponse> handleFlow(DailyQuestionRequest req, DailyCount count, UserId userId) {
        List<Question> collected = new ArrayList<>();
        if (count.getValue() == 1) {
            FlowPhase startPhase = getNextPhaseForUser(req.getUserId(), req.getJobRole().name());
            FlowPhase phase = startPhase;
            for (int i = 0; i < 5; i++) {
                List<Question> one = questionRepository.findRandomByFlowPhaseExcludingSolved(
                        req.getJobRole().name(), phase.name(), userId.getValue());
                if (!one.isEmpty()) {
                    collected.add(one.get(0));
                    markAssigned(req.getUserId(), req.getJobRole().name(), phase);
                    break;
                }
                phase = phase.next();
            }
        } else {
            for (FlowPhase p : EnumSet.of(FlowPhase.INTRO, FlowPhase.MOTIVATION, FlowPhase.TECH1, FlowPhase.TECH2, FlowPhase.PERSONALITY)) {
                List<Question> one = questionRepository.findRandomByFlowPhaseExcludingSolved(
                        req.getJobRole().name(), p.name(), userId.getValue());
                if (!one.isEmpty()) { collected.add(one.get(0)); }
            }
        }

        if (collected.isEmpty()) return Optional.empty();
        return Optional.of(new DailyQuestionResponse(LocalDate.now(), req.getMode(), map(collected)));
    }

    private FlowPhase getNextPhaseForUser(Long userId, String jobRole) {
        return userFlowProgressRepository.findById(new com.knuissant.dailyq.Question.entity.UserFlowProgressId(userId,
                        com.knuissant.dailyq.Question.enums.JobRole.valueOf(jobRole)))
                .map(UserFlowProgress::getLastPhase)
                .map(FlowPhase::next)
                .orElse(FlowPhase.INTRO);
    }

    @Transactional
    protected void markAssigned(Long userId, String jobRole, FlowPhase phase) {
        var id = new com.knuissant.dailyq.Question.entity.UserFlowProgressId(userId,
                com.knuissant.dailyq.Question.enums.JobRole.valueOf(jobRole));
        UserFlowProgress progress = userFlowProgressRepository.findById(id)
                .orElse(new UserFlowProgress(userId,
                        com.knuissant.dailyq.Question.enums.JobRole.valueOf(jobRole), FlowPhase.INTRO, null));
        progress.markAssignedToday(phase);
        userFlowProgressRepository.save(progress);
    }

    private List<QuestionResponse> map(List<Question> list) {
        List<QuestionResponse> result = new ArrayList<>();
        for (Question q : list) {
            result.add(new QuestionResponse(q.getId(), q.getContent(), q.getJob().getName(), q.getType(), q.getFlowPhase()));
        }
        return result;
    }
}


