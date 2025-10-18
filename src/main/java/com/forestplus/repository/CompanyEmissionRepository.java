package com.forestplus.repository;

import com.forestplus.entity.CompanyEmissionEntity;
import com.forestplus.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyEmissionRepository extends JpaRepository<CompanyEmissionEntity, Long> {

    List<CompanyEmissionEntity> findByCompany(CompanyEntity company);

    List<CompanyEmissionEntity> findByCompanyId(Long companyId);

    List<CompanyEmissionEntity> findByCompanyIdAndYear(Long companyId, Integer year);
}
