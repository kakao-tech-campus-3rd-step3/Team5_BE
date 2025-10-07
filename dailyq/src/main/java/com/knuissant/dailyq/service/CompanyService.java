package com.knuissant.dailyq.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.companies.Company;
import com.knuissant.dailyq.dto.companies.CompanyListResponse;
import com.knuissant.dailyq.repository.CompanyRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyService {

    private final CompanyRepository companyRepository;

    public List<CompanyListResponse> findAllCompanies() {

        List<Company> companies = companyRepository.findAll();

        return companies.stream()
                .map(CompanyListResponse::from).toList();
    }
}
