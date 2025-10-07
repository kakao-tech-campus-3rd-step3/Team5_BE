package com.knuissant.dailyq.dto.companies;

import com.knuissant.dailyq.domain.companies.Company;

public record CompanyListResponse (
        Long companyId,
        String companyName
){
    public static CompanyListResponse from(Company company) {
        return new CompanyListResponse(
                company.getId(),
                company.getName()
        );
    }
}
