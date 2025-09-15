package com.knuissant.dailyq.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.knuissant.dailyq.dto.users.JobResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.JobRepository;
import com.knuissant.dailyq.repository.OccupationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobService {

    private final JobRepository jobRepository;
    private final OccupationRepository occupationRepository;

    public List<JobResponse> findJobsByOccupation(Long occupationId) {
        occupationRepository.findById(occupationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OCCUPATION_NOT_FOUND));

        // 2. 상위 직군이 존재하므로, 해당 직군에 속한 하위 직업(Job) 목록을 조회하여 반환합니다.
        return jobRepository.findByOccupationId(occupationId).stream()
                .map(JobResponse::from)
                .collect(Collectors.toList());
    }
}