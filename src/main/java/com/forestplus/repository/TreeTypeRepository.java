package com.forestplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.forestplus.entity.TreeTypeEntity;

@Repository
public interface TreeTypeRepository extends JpaRepository<TreeTypeEntity, Long> {

    // ✅ Aquí podrías agregar métodos personalizados si los necesitas
    // Por ejemplo: buscar por nombre
    // Optional<TreeTypeEntity> findByName(String name);
}
