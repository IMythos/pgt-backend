package com.portable.microservices.ms_tracking.picking.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;

import com.portable.microservices.ms_tracking.picking.domain.model.OrdenPick;
import com.portable.microservices.ms_tracking.picking.infrastructure.persistence.entity.OrdenPickJpaEntity;

@Mapper(componentModel = "spring")
public interface OrdenPickPersistenceMapper {

    OrdenPick toDomain(OrdenPickJpaEntity entity);

    OrdenPickJpaEntity toEntity(OrdenPick domain);
}
