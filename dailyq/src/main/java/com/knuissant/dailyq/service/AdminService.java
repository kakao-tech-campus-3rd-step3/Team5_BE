package com.knuissant.dailyq.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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
                .toList();
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
        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, userId);
        }
    }

    /* =========================
       Occupation Management
       ========================= */

    @Transactional(readOnly = true)
    public List<OccupationManagementDto.OccupationResponse> getAllOccupations() {
        return occupationRepository.findAll().stream()
                .map(OccupationManagementDto.OccupationResponse::from)
                .toList();
    }

    public OccupationManagementDto.OccupationResponse createOccupation(OccupationManagementDto.OccupationCreateRequest request) {
        try {
            Occupation savedOccupation = occupationRepository.save(Occupation.create(request.occupationName()));
            return OccupationManagementDto.OccupationResponse.from(savedOccupation);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.OCCUPATION_ALREADY_EXISTS, request.occupationName());
        }
    }

    public OccupationManagementDto.OccupationResponse updateOccupation(Long occupationId, OccupationManagementDto.OccupationUpdateRequest request) {
        Occupation occupation = occupationRepository.findById(occupationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OCCUPATION_NOT_FOUND));

        occupation.updateName(request.occupationName());
        try {
            occupationRepository.flush(); // 변경 사항을 DB에 즉시 반영하여 제약 조건 위반 검사
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.OCCUPATION_ALREADY_EXISTS, request.occupationName());
        }
        return OccupationManagementDto.OccupationResponse.from(occupation);
    }

    public void deleteOccupation(Long occupationId) {
        Occupation occupation = occupationRepository.findById(occupationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OCCUPATION_NOT_FOUND));
        if (jobRepository.existsByOccupation(occupation)) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_OCCUPATION_WITH_JOBS);
        }
        occupationRepository.delete(occupation);
    }


    /* =========================
       Job Management
       ========================= */

    @Transactional(readOnly = true)
    public List<JobManagementDto.JobResponse> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(JobManagementDto.JobResponse::from)
                .toList();
    }

    public JobManagementDto.JobResponse createJob(JobManagementDto.JobCreateRequest request) {
        Occupation occupation = occupationRepository.findById(request.occupationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.OCCUPATION_NOT_FOUND));

        try {
            Job savedJob = jobRepository.save(Job.create(request.jobName(), occupation));
            return JobManagementDto.JobResponse.from(savedJob);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.JOB_ALREADY_EXISTS, request.jobName());
        }
    }

    public JobManagementDto.JobResponse updateJob(Long jobId, JobManagementDto.JobUpdateRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOB_NOT_FOUND));

        Occupation occupation = occupationRepository.findById(request.occupationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.OCCUPATION_NOT_FOUND));

        job.update(request.jobName(), occupation);
        try {
            jobRepository.flush(); // 변경 사항 즉시 반영
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.JOB_ALREADY_EXISTS, request.jobName());
        }
        return JobManagementDto.JobResponse.from(job);
    }

    public void deleteJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOB_NOT_FOUND));
        if (userPreferencesRepository.existsByUserJob(job)) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_JOB_IN_USE);
        }
        jobRepository.delete(job);
    }

    /* =========================
       Question Management
       ========================= */

    @Transactional(readOnly = true)
    public List<QuestionManagementDto.QuestionResponse> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(QuestionManagementDto.QuestionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuestionManagementDto.QuestionDetailResponse getQuestionById(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND, questionId));
        return QuestionManagementDto.QuestionDetailResponse.from(question);
    }

    public QuestionManagementDto.QuestionDetailResponse createQuestion(QuestionManagementDto.QuestionCreateRequest request) {
        if (questionRepository.existsByQuestionText(request.questionText())) {
            throw new BusinessException(ErrorCode.QUESTION_ALREADY_EXISTS);
        }

        Set<Long> jobIds = new HashSet<>(request.jobIds()); // 중복 ID 제거
        List<Job> jobs = jobRepository.findAllById(jobIds);
        if (jobs.size() != jobIds.size()) {
            throw new BusinessException(ErrorCode.JOB_NOT_FOUND, "일부 직업을 찾을 수 없습니다.");
        }

        try {
            Question savedQuestion = questionRepository.save(Question.create(request.questionText(), request.questionType(), new HashSet<>(jobs), request.enabled()));
            return QuestionManagementDto.QuestionDetailResponse.from(savedQuestion);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.QUESTION_ALREADY_EXISTS, request.questionText());
        }
    }

    public QuestionManagementDto.QuestionDetailResponse updateQuestion(Long questionId, QuestionManagementDto.QuestionUpdateRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        if (!question.getQuestionText().equals(request.questionText()) && questionRepository.existsByQuestionText(request.questionText())) {
            throw new BusinessException(ErrorCode.QUESTION_ALREADY_EXISTS);
        }

        Set<Long> jobIds = new HashSet<>(request.jobIds()); // 중복 ID 제거
        List<Job> jobs = jobRepository.findAllById(jobIds);
        if (jobs.size() != jobIds.size()) {
            throw new BusinessException(ErrorCode.JOB_NOT_FOUND, "일부 직업을 찾을 수 없습니다.");
        }

        question.update(request.questionText(), request.questionType(), request.enabled(), new HashSet<>(jobs));

        try {
            questionRepository.flush(); // 변경 사항 즉시 반영
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.QUESTION_ALREADY_EXISTS, request.questionText());
        }

        return QuestionManagementDto.QuestionDetailResponse.from(question);
    }

    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
        if (answerRepository.existsByQuestion(question)) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_QUESTION_WITH_ANSWERS);
        }
        questionRepository.delete(question);
    }
}