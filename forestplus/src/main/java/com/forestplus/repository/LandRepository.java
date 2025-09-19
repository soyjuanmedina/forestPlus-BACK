package com.forestplus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.forestplus.entity.LandEntity;

@Repository
public interface LandRepository extends JpaRepository<LandEntity, Long> {
    List<LandEntity> findByCompanyId(Long companyId);
}
