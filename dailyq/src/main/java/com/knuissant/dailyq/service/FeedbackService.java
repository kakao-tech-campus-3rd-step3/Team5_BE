package com.knuissant.dailyq.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.companies.Company;
import com.knuissant.dailyq.domain.feedbacks.Feedback;
import com.knuissant.dailyq.domain.feedbacks.FeedbackStatus;
import com.knuissant.dailyq.domain.feedbacks.FeedbackType;
import com.knuissant.dailyq.dto.feedbacks.FeedbackResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.external.gpt.GptClient;
import com.knuissant.dailyq.external.gpt.PromptManager;
import com.knuissant.dailyq.external.gpt.PromptType;
import com.knuissant.dailyq.repository.AnswerRepository;
import com.knuissant.dailyq.repository.CompanyRepository;
import com.knuissant.dailyq.repository.FeedbackRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final GptClient gptClient;
    private final FeedbackRepository feedbackRepository;
    private final CompanyRepository companyRepository;
    private final AnswerRepository answerRepository;
    private final PromptManager promptManager;
    private final FeedbackUpdateService feedbackUpdateService;
    private final ObjectMapper objectMapper;

    @Transactional
    public Feedback createPendingFeedback(Answer answer) {

        Feedback feedback = Feedback.createGeneralFeedback(answer, FeedbackStatus.PENDING);
        return feedbackRepository.save(feedback);
    }

    @Transactional
    public Long requestCultureFitFeedback(Long answerId, Long companyId) {

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

        Feedback feedback = Feedback.createCultureFitFeedback(answer, company,
                FeedbackStatus.PENDING);
        return feedbackRepository.save(feedback).getId();
    }

    public FeedbackResponse generateFeedback(Long feedbackId) {

        Feedback feedback = feedbackRepository.findWithDetailsById(feedbackId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FEEDBACK_NOT_FOUND));

        if (feedback.getStatus() == FeedbackStatus.DONE) {
            return FeedbackResponse.from(feedback.getContent(), objectMapper);
        }

        feedbackUpdateService.changeStatusToProcessing(feedbackId);

        String question = feedback.getAnswer().getQuestion().getQuestionText();
        String answer = feedback.getAnswer().getAnswerText();

        String systemPrompt;
        String userPrompt;

        if (feedback.getFeedbackType() == FeedbackType.CULTURE_FIT) {
            String cultureFitText = feedback.getCompany().getCultureFitText();
            systemPrompt = promptManager.load(PromptType.CULTURE_FIT_SYSTEM);
            userPrompt = promptManager.load(PromptType.FEEDBACK_USER, cultureFitText, question,
                    answer);
        } else {
            systemPrompt = promptManager.load(PromptType.FEEDBACK_SYSTEM);
            userPrompt = promptManager.load(PromptType.FEEDBACK_USER, question, answer);
        }

        long startTime = System.currentTimeMillis();
        try {
            FeedbackResponse feedbackResponse = gptClient.call(systemPrompt, userPrompt);
            long latencyMs = System.currentTimeMillis() - startTime;

            feedbackUpdateService.updateFeedbackSuccess(feedbackId, feedbackResponse, latencyMs);
            return feedbackResponse;

        } catch (Exception e) {
            try {
                feedbackUpdateService.updateFeedbackFailure(feedbackId);
            } catch (Exception failureUpdateEx) {
                log.error("Failed to update status to FAILED for feedbackId {}, Original error: {}",
                        feedbackId, e.getMessage());
            }
            throw e;
        }
    }
}
