package com.forestplus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.forestplus.entity.CoordinateEntity;

@Repository
public interface CoordinateRepository extends JpaRepository<CoordinateEntity, Long> {
    List<CoordinateEntity> findByLandId(Long landId);
}
