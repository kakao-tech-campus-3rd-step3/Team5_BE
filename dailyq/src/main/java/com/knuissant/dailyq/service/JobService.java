package com.knuissant.dailyq.service;

import com.knuissant.dailyq.dto.JobResponse;
import com.knuissant.dailyq.repository.JobRepository;
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

    /**
     * 특정 상위 직군에 속한 하위 직군 목록을 조회합니다.
     * @param occupationId 상위 직군 ID
     * @return 하위 직군 목록
     */
    public List<JobResponse> findJobsByOccupation(Long occupationId) {
        return jobRepository.findByOccupationId(occupationId).stream()
                .map(j -> new JobResponse(j.getId(), j.getName(), j.getOccupation().getId()))
                .collect(Collectors.toList());
    }
}

