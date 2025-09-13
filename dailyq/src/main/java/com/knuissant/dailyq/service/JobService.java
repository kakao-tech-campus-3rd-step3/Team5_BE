package com.knuissant.dailyq.service;

import com.knuissant.dailyq.dto.JobResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.JobRepository;
import com.knuissant.dailyq.repository.OccupationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobService {

    private final JobRepository jobRepository;
    private final OccupationRepository occupationRepository;

    public List<JobResponse> findJobsByOccupation(Long occupationId) {
        // 1. 상위 직군(Occupation)이 존재하는지 먼저 확인합니다.
        if (!occupationRepository.existsById(occupationId)) {
            // 2. 존재하지 않으면 OCCUPATION_NOT_FOUND 예외를 발생시킵니다.
            throw new BusinessException(ErrorCode.OCCUPATION_NOT_FOUND);
        }

        // 3. 상위 직군이 존재하면, 해당 직군에 속한 하위 직업(Job) 목록을 조회하여 반환합니다.
        return jobRepository.findByOccupationId(occupationId).stream()
                .map(job -> JobResponse.from(job))
                .collect(Collectors.toList());
    }
}