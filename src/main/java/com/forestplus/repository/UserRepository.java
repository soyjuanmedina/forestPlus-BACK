package com.forestplus.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.forestplus.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByEmail(String email);
	Optional<UserEntity> findByUuid(String uuid);
	Page<UserEntity> findByRole(String role, Pageable pageable);
	Page<UserEntity> findByCompanyId(Long companyId, Pageable pageable);
	Page<UserEntity> findByRoleAndCompanyId(String role, Long companyId, Pageable pageable);
}
