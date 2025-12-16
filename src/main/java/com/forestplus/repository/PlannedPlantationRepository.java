package com.forestplus.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.forestplus.entity.PlannedPlantationEntity;

@Repository
public interface PlannedPlantationRepository
        extends JpaRepository<PlannedPlantationEntity, Long> {

    /**
     * Plantaciones asociadas a un terreno
     */
    List<PlannedPlantationEntity> findByLandId(Long landId);

    /**
     * Plantaciones sin terreno asignado
     */
    List<PlannedPlantationEntity> findByLandIsNull();

    /**
     * Plantaciones previstas entre dos fechas
     */
    List<PlannedPlantationEntity> findByPlannedDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Plantaciones ya ejecutadas
     */
    List<PlannedPlantationEntity> findByEffectiveDateIsNotNull();

    /**
     * Plantaciones pendientes de ejecutar
     */
    List<PlannedPlantationEntity> findByEffectiveDateIsNull();

    /**
     * Plantaciones previstas para un terreno y aún no ejecutadas
     */
    @Query("""
        SELECT pp
        FROM PlannedPlantationEntity pp
        WHERE pp.land.id = :landId
          AND pp.effectiveDate IS NULL
        """)
    List<PlannedPlantationEntity> findPendingByLand(
            @Param("landId") Long landId
    );

    /**
     * Plantaciones ejecutadas fuera de la fecha prevista
     */
    @Query("""
        SELECT pp
        FROM PlannedPlantationEntity pp
        WHERE pp.effectiveDate IS NOT NULL
          AND pp.effectiveDate <> pp.plannedDate
        """)
    List<PlannedPlantationEntity> findExecutedWithDateDeviation();
    
    List<PlannedPlantationEntity> findAllByIsActiveTrue();
}
