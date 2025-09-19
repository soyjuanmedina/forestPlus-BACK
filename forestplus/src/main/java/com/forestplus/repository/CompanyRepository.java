package com.forestplus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.forestplus.entity.CompanyEntity;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    // Métodos adicionales si quieres filtrar por user_id, por ejemplo:
    List<CompanyEntity> findByUserId(Long userId);
}
