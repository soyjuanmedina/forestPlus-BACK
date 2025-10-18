package com.forestplus.repository;

import com.forestplus.entity.CompanyCompensationEntity;
import com.forestplus.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyCompensationRepository extends JpaRepository<CompanyCompensationEntity, Long> {

    List<CompanyCompensationEntity> findByCompany(CompanyEntity company);

    List<CompanyCompensationEntity> findByCompanyId(Long companyId);

    List<CompanyCompensationEntity> findByCompanyIdAndYear(Long companyId, Integer year);
}
