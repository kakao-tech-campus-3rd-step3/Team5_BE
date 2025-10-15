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
import com.knuissant.dailyq.repository.JobRepository;
import com.knuissant.dailyq.repository.OccupationRepository;
import com.knuissant.dailyq.repository.QuestionRepository;
import com.knuissant.dailyq.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final OccupationRepository occupationRepository;
    private final JobRepository jobRepository;
    private final QuestionRepository questionRepository;


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

        User updatedUser = userRepository.save(user);
        return UserManagementDto.UserResponse.from(updatedUser);
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
        Occupation newOccupation = Occupation.builder()
                .name(request.occupationName())
                .build();
        Occupation savedOccupation = occupationRepository.save(newOccupation);
        return OccupationManagementDto.OccupationResponse.from(savedOccupation);
    }

    public OccupationManagementDto.OccupationResponse updateOccupation(Long occupationId, OccupationManagementDto.OccupationUpdateRequest request) {
        Occupation occupation = occupationRepository.findById(occupationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OCCUPATION_NOT_FOUND));

        Occupation updatedOccupation = occupationRepository.save(Occupation.builder()
                .id(occupation.getId())
                .name(request.occupationName())
                .build());
        return OccupationManagementDto.OccupationResponse.from(updatedOccupation);
    }

    public void deleteOccupation(Long occupationId) {
        if (!occupationRepository.existsById(occupationId)) {
            throw new BusinessException(ErrorCode.OCCUPATION_NOT_FOUND);
        }
        occupationRepository.deleteById(occupationId);
    }


    /* =========================
       Job Management
       ========================= */

    public JobManagementDto.JobResponse createJob(JobManagementDto.JobCreateRequest request) {
        Occupation occupation = occupationRepository.findById(request.occupationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.OCCUPATION_NOT_FOUND));

        Job newJob = Job.builder()
                .name(request.jobName())
                .occupation(occupation)
                .build();

        Job savedJob = jobRepository.save(newJob);
        return JobManagementDto.JobResponse.from(savedJob);
    }

    public JobManagementDto.JobResponse updateJob(Long jobId, JobManagementDto.JobUpdateRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOB_NOT_FOUND));

        Occupation occupation = occupationRepository.findById(request.occupationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.OCCUPATION_NOT_FOUND));

        Job updatedJob = jobRepository.save(Job.builder()
                .id(job.getId())
                .name(request.jobName())
                .occupation(occupation)
                .build());

        return JobManagementDto.JobResponse.from(updatedJob);
    }

    public void deleteJob(Long jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new BusinessException(ErrorCode.JOB_NOT_FOUND);
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
        List<Job> jobs = jobRepository.findAllById(request.jobIds());
        if (jobs.size() != request.jobIds().size()) {
            throw new BusinessException(ErrorCode.JOB_NOT_FOUND, "일부 직업을 찾을 수 없습니다.");
        }

        Question newQuestion = Question.builder()
                .questionText(request.questionText())
                .questionType(request.questionType())
                .enabled(true)
                .jobs(new HashSet<>(jobs))
                .build();

        Question savedQuestion = questionRepository.save(newQuestion);
        return QuestionManagementDto.QuestionDetailResponse.from(savedQuestion);
    }

    public QuestionManagementDto.QuestionDetailResponse updateQuestion(Long questionId, QuestionManagementDto.QuestionUpdateRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        List<Job> jobs = jobRepository.findAllById(request.jobIds());
        if (jobs.size() != request.jobIds().size()) {
            throw new BusinessException(ErrorCode.JOB_NOT_FOUND, "일부 직업을 찾을 수 없습니다.");
        }

        Question updatedQuestion = questionRepository.save(Question.builder()
                .id(question.getId())
                .questionText(request.questionText())
                .questionType(request.questionType())
                .enabled(request.enabled())
                .jobs(new HashSet<>(jobs))
                .build());

        return QuestionManagementDto.QuestionDetailResponse.from(updatedQuestion);
    }

    public void deleteQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new BusinessException(ErrorCode.QUESTION_NOT_FOUND);
        }
        questionRepository.deleteById(questionId);
    }
}