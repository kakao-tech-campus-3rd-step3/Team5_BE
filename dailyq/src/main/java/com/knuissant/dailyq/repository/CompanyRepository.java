package com.knuissant.dailyq.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.knuissant.dailyq.domain.companies.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {

}
