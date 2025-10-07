package com.knuissant.dailyq.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.dto.companies.CompanyListResponse;
import com.knuissant.dailyq.service.CompanyService;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<List<CompanyListResponse>> getCompanies() {

        List<CompanyListResponse> responses = companyService.findAllCompanies();

        return ResponseEntity.ok(responses);
    }

}
