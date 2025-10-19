package com.forestplus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.forestplus.entity.CompanyCO2YearlyEntity;

@Repository
public interface CompanyCO2YearlyRepository extends JpaRepository<CompanyCO2YearlyEntity, Long> {
    List<CompanyCO2YearlyEntity> findByCompanyId(Long companyId);
    Optional<CompanyCO2YearlyEntity> findByCompanyIdAndYear(Long companyId, Integer year);
}