package com.knuissant.dailyq.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.questions.FollowUpQuestion;
import com.knuissant.dailyq.dto.questions.FollowUpQuestionResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.exception.InfraException;
import com.knuissant.dailyq.external.gpt.GptClient;
import com.knuissant.dailyq.external.gpt.PromptManager;
import com.knuissant.dailyq.external.gpt.PromptType;
import com.knuissant.dailyq.repository.AnswerRepository;
import com.knuissant.dailyq.repository.FollowUpQuestionRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowUpQuestionService {

    private final FollowUpQuestionRepository followUpQuestionRepository;
    private final AnswerRepository answerRepository;
    private final GptClient gptClient;
    private final PromptManager promptManager;

    @Transactional
    public void generateFollowUpQuestions(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));

        // 꼬리질문의 꼬리질문은 생성하지 않음
        if (isAnswerToFollowUpQuestion(answer)) {
            throw new BusinessException(ErrorCode.FOLLOWUP_QUESTION_NOT_FOUND, "이미 꼬리질문에 대한 답변입니다.");
        }

        // 이미 해당 답변에 대한 꼬리질문이 있는지 확인
        if (followUpQuestionRepository.existsByAnswer(answer)) {
            throw new BusinessException(ErrorCode.FOLLOWUP_QUESTION_NOT_FOUND, "이미 꼬리질문이 존재합니다.");
        }

        try {
            // AI로 꼬리질문 생성
            String systemPrompt = promptManager.load(PromptType.FOLLOWUP_SYSTEM);
            String userPrompt = promptManager.load(PromptType.FOLLOWUP_USER, 
                answer.getQuestion().getQuestionText(), 
                answer.getAnswerText());

            FollowUpQuestionResponse response = gptClient.callForFollowUp(systemPrompt, userPrompt);

            // 꼬리질문들을 DB에 저장
            List<FollowUpQuestion> followUpQuestions = response.getQuestions().stream()
                .map(questionText -> FollowUpQuestion.create(answer.getUser(), answer, questionText))
                .toList();

            followUpQuestionRepository.saveAll(followUpQuestions);

        } catch (Exception e) {
            log.error("Failed to generate follow-up questions for answer {}", answerId, e);
            throw new InfraException(ErrorCode.GPT_API_COMMUNICATION_ERROR, "꼬리질문 생성에 실패했습니다.");
        }
    }

    /**
     * 답변이 꼬리질문에 대한 답변인지 확인
     */
    private boolean isAnswerToFollowUpQuestion(Answer answer) {
        return followUpQuestionRepository.isAnswerToFollowUpQuestion(answer);
    }

    /**
     * 사용자의 미답변 꼬리질문 조회
     */
    @Transactional(readOnly = true)
    public List<FollowUpQuestion> getUnansweredFollowUpQuestions(Long userId) {
        return followUpQuestionRepository.findByUserIdAndIsAnsweredFalseOrderByCreatedAtAsc(userId, 
            org.springframework.data.domain.PageRequest.of(0, 10));
    }

    /**
     * 꼬리질문을 답변 완료로 표시
     */
    @Transactional
    public void markFollowUpQuestionAsAnswered(Long followUpQuestionId) {
        FollowUpQuestion followUpQuestion = followUpQuestionRepository.findById(followUpQuestionId)
            .orElseThrow(() -> new BusinessException(ErrorCode.FOLLOWUP_QUESTION_NOT_FOUND));
        
        followUpQuestion.markAsAnswered();
    }
}
