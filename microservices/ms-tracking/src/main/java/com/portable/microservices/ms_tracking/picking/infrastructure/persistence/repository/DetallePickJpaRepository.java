package com.portable.microservices.ms_tracking.picking.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portable.microservices.ms_tracking.picking.infrastructure.persistence.entity.DetallePickJpaEntity;

public interface DetallePickJpaRepository extends JpaRepository<DetallePickJpaEntity, UUID> {
    List<DetallePickJpaEntity> findByOrden_IdOrden(UUID idOrden);
}
