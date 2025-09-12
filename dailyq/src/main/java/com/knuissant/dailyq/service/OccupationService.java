package com.knuissant.dailyq.service;

import com.knuissant.dailyq.dto.OccupationResponse;
import com.knuissant.dailyq.repository.OccupationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OccupationService {

    private final OccupationRepository occupationRepository;

    /**
     * 모든 상위 직군 목록을 조회합니다.
     * @return 상위 직군 목록
     */
    public List<OccupationResponse> findAllOccupations() {
        return occupationRepository.findAll().stream()
                .map(o -> new OccupationResponse(o.getId(), o.getName()))
                .collect(Collectors.toList());
    }
}
