package com.portable.microservices.ms_tracking.picking.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portable.microservices.ms_tracking.picking.infrastructure.persistence.entity.RutaPickJpaEntity;

public interface RutaPickJpaRepository extends JpaRepository<RutaPickJpaEntity, UUID> {
    Optional<RutaPickJpaEntity> findByIdOrden(UUID idOrden);
}
