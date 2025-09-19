package com.forestplus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.forestplus.entity.TreeEntity;

@Repository
public interface TreeRepository extends JpaRepository<TreeEntity, Long> {
    List<TreeEntity> findByLandId(Long landId);
}
