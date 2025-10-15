package com.knuissant.dailyq.service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.jobs.Job;
import com.knuissant.dailyq.domain.jobs.Occupation;
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.dto.admin.JobManagementDto;
import com.knuissant.dailyq.dto.admin.OccupationManagementDto;
import com.knuissant.dailyq.dto.admin.QuestionManagementDto;
import com.knuissant.dailyq.dto.admin.UserManagementDto;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final OccupationRepository occupationRepository;
    private final JobRepository jobRepository;
    private final QuestionRepository questionRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final AnswerRepository answerRepository;

    /* =========================
       User Management
       ========================= */
    @Transactional(readOnly = true)
    public List<UserManagementDto.UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserManagementDto.UserResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserManagementDto.UserDetailResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserManagementDto.UserDetailResponse.from(user);
    }

    public UserManagementDto.UserResponse updateUser(Long userId, UserManagementDto.UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.updateName(request.name());
        user.updateRole(request.role());

        return UserManagementDto.UserResponse.from(user);
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }

    /* =========================
       Occupation Management
       ========================= */

    public OccupationManagementDto.OccupationResponse createOccupation(OccupationManagementDto.OccupationCreateRequest request) {
        if (occupationRepository.existsByName(request.occupationName())) {
            throw new BusinessException(ErrorCode.OCCUPATION_ALREADY_EXISTS);
        }
        Occupation savedOccupation = occupationRepository.save(Occupation.create(request.occupationName()));
        return OccupationManagementDto.OccupationResponse.from(savedOccupation);
    }

    public OccupationManagementDto.OccupationResponse updateOccupation(Long occupationId, OccupationManagementDto.OccupationUpdateRequest request) {
        Occupation occupation = occupationRepository.findById(occupationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OCCUPATION_NOT_FOUND));

        if (!occupation.getName().equals(request.occupationName()) && occupationRepository.existsByName(request.occupationName())) {
            throw new BusinessException(ErrorCode.OCCUPATION_ALREADY_EXISTS);
        }
        occupation.updateName(request.occupationName());
        return OccupationManagementDto.OccupationResponse.from(occupation);
    }

    public void deleteOccupation(Long occupationId) {
        Occupation occupation = occupationRepository.findById(occupationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OCCUPATION_NOT_FOUND));
        if (jobRepository.existsByOccupation(occupation)) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_OCCUPATION_WITH_JOBS);
        }
        occupationRepository.deleteById(occupationId);
    }


    /* =========================
       Job Management
       ========================= */

    public JobManagementDto.JobResponse createJob(JobManagementDto.JobCreateRequest request) {
        if (jobRepository.existsByName(request.jobName())) {
            throw new BusinessException(ErrorCode.JOB_ALREADY_EXISTS);
        }
        Occupation occupation = occupationRepository.findById(request.occupationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.OCCUPATION_NOT_FOUND));

        Job savedJob = jobRepository.save(Job.create(request.jobName(), occupation));
        return JobManagementDto.JobResponse.from(savedJob);
    }

    public JobManagementDto.JobResponse updateJob(Long jobId, JobManagementDto.JobUpdateRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOB_NOT_FOUND));

        if (!job.getName().equals(request.jobName()) && jobRepository.existsByName(request.jobName())) {
            throw new BusinessException(ErrorCode.JOB_ALREADY_EXISTS);
        }
        Occupation occupation = occupationRepository.findById(request.occupationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.OCCUPATION_NOT_FOUND));

        job.update(request.jobName(), occupation);
        return JobManagementDto.JobResponse.from(job);
    }

    public void deleteJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOB_NOT_FOUND));
        if (userPreferencesRepository.existsByUserJob(job)) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_JOB_IN_USE);
        }
        jobRepository.deleteById(jobId);
    }

    /* =========================
       Question Management
       ========================= */

    @Transactional(readOnly = true)
    public List<QuestionManagementDto.QuestionResponse> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(QuestionManagementDto.QuestionResponse::from)
                .collect(Collectors.toList());
    }

    public QuestionManagementDto.QuestionDetailResponse createQuestion(QuestionManagementDto.QuestionCreateRequest request) {
        if (questionRepository.existsByQuestionText(request.questionText())) {
            throw new BusinessException(ErrorCode.QUESTION_ALREADY_EXISTS);
        }
        List<Job> jobs = jobRepository.findAllById(request.jobIds());
        if (jobs.size() != request.jobIds().size()) {
            throw new BusinessException(ErrorCode.JOB_NOT_FOUND, "일부 직업을 찾을 수 없습니다.");
        }

        Question savedQuestion = questionRepository.save(Question.create(request.questionText(), request.questionType(), new HashSet<>(jobs)));
        return QuestionManagementDto.QuestionDetailResponse.from(savedQuestion);
    }

    public QuestionManagementDto.QuestionDetailResponse updateQuestion(Long questionId, QuestionManagementDto.QuestionUpdateRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        if (!question.getQuestionText().equals(request.questionText()) && questionRepository.existsByQuestionText(request.questionText())) {
            throw new BusinessException(ErrorCode.QUESTION_ALREADY_EXISTS);
        }
        List<Job> jobs = jobRepository.findAllById(request.jobIds());
        if (jobs.size() != request.jobIds().size()) {
            throw new BusinessException(ErrorCode.JOB_NOT_FOUND, "일부 직업을 찾을 수 없습니다.");
        }

        question.update(request.questionText(), request.questionType(), request.enabled(), new HashSet<>(jobs));
        return QuestionManagementDto.QuestionDetailResponse.from(question);
    }

    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
        if (answerRepository.existsByQuestion(question)) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_QUESTION_WITH_ANSWERS);
        }
        questionRepository.deleteById(questionId);
    }
}