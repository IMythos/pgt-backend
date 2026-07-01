package com.portable.microservices.ms_tracking.picking.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portable.microservices.ms_tracking.picking.infrastructure.persistence.entity.AuditoriaPickJpaEntity;

public interface AuditoriaPickJpaRepository extends JpaRepository<AuditoriaPickJpaEntity, UUID> {
}
