package com.knuissant.dailyq.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.knuissant.dailyq.domain.feedbacks.Feedback;
import com.knuissant.dailyq.dto.feedbacks.FeedbackResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.exception.InfraException;
import com.knuissant.dailyq.external.gpt.GptClient;
import com.knuissant.dailyq.external.gpt.PromptManager;
import com.knuissant.dailyq.external.gpt.PromptType;
import com.knuissant.dailyq.repository.FeedbackRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final GptClient gptClient;
    private final FeedbackRepository feedbackRepository;
    private final PromptManager promptManager;
    private final FeedbackUpdateService feedbackUpdateService;
    private final FollowUpQuestionService followUpQuestionService;

    public FeedbackResponse generateFeedback(Long feedbackId) {

        Feedback feedback = feedbackRepository.findWithDetailsById(feedbackId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FEEDBACK_NOT_FOUND));
        String question = feedback.getAnswer().getQuestion().getQuestionText();
        String answer = feedback.getAnswer().getAnswerText();

        String systemPrompt = promptManager.load(PromptType.FEEDBACK_SYSTEM);
        String userPrompt = promptManager.load(PromptType.FEEDBACK_USER, question, answer);

        long startTime = System.currentTimeMillis();
        try {
            FeedbackResponse feedbackResponse = gptClient.call(systemPrompt, userPrompt);
            long latencyMs = System.currentTimeMillis() - startTime;

            feedbackUpdateService.updateFeedbackSuccess(feedbackId, feedbackResponse, latencyMs);
            
            // 응답 반환을 우선하고, 커밋 후 비동기로 꼬리질문 생성
            if (feedback.getAnswer().getFollowUpQuestion() == null) {
                followUpQuestionAfterCommit(feedback.getAnswer().getId());
            } else {
                log.debug("Skip follow-up generation for follow-up answer: {}", feedback.getAnswer().getId());
            }
            
            return feedbackResponse;

        } catch (Exception e) {
            try {
                feedbackUpdateService.updateFeedbackFailure(feedbackId);
            } catch (Exception failureUpdateEx) {
                throw new InfraException(ErrorCode.INTERNAL_SERVER_ERROR, "피드백 상태 업데이트 실패", failureUpdateEx);
            }
            throw new InfraException(ErrorCode.INTERNAL_SERVER_ERROR, "피드백 생성 처리 중 오류", e);
        }
    }

    @org.springframework.transaction.event.TransactionalEventListener(phase = org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT)
    @org.springframework.scheduling.annotation.Async
    public void followUpQuestionAfterCommit(Long answerId) {
        try {
            followUpQuestionService.generateFollowUpQuestions(answerId);
        } catch (Exception ex) {
            throw new InfraException(ErrorCode.GPT_API_COMMUNICATION_ERROR, "꼬리질문 생성 실패", ex);
        }
    }
}
