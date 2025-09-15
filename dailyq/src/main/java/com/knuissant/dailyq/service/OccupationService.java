package com.knuissant.dailyq.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.knuissant.dailyq.dto.users.OccupationResponse;
import com.knuissant.dailyq.repository.OccupationRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OccupationService {

    private final OccupationRepository occupationRepository;

    public List<OccupationResponse> findAllOccupations() {
        return occupationRepository.findAll().stream()
                .map(o -> new OccupationResponse(o.getId(), o.getName()))
                .collect(Collectors.toList());
    }
}
