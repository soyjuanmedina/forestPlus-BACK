package com.forestplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.forestplus.entity.OrderEntity;

@Repository
public interface OrdersRepository extends JpaRepository<OrderEntity, Long> {
    // Aquí puedes añadir consultas personalizadas si las necesitas
}
