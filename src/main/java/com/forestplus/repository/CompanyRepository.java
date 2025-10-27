package com.forestplus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.forestplus.entity.CompanyEntity;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

    @Query("""
        SELECT c
        FROM CompanyEntity c
        JOIN c.users u
        WHERE u.id = :adminId AND u.role = 'COMPANY_ADMIN'
    """)
    List<CompanyEntity> findByAdminId(@Param("adminId") Long adminId);
}