package com.portable.microservices.ms_tracking.picking.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portable.microservices.ms_tracking.picking.infrastructure.persistence.entity.OrdenPickJpaEntity;

public interface OrdenPickJpaRepository extends JpaRepository<OrdenPickJpaEntity, UUID> {
    List<OrdenPickJpaEntity> findByEstado(String estado);
    java.util.Optional<OrdenPickJpaEntity> findByDocRef(String docRef);
}
