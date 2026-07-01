package com.portable.microservices.ms_inventory.locations.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.portable.microservices.ms_inventory.locations.domain.model.Location;
import com.portable.microservices.ms_inventory.locations.infrastructure.persistence.entity.LocationJpaEntity;

@Component
public class LocationPersistenceMapper {

    public Location toDomain(LocationJpaEntity entity) {
        if (entity == null) return null;
        return Location.builder()
                .idLocacion(entity.getIdLocacion())
                .idAlmacen(entity.getIdAlmacen())
                .zona(entity.getZona())
                .pasillo(entity.getPasillo())
                .estante(entity.getEstante())
                .codBarras(entity.getCodBarras())
                .capacidad(entity.getCapacidad())
                .posX(entity.getPosX())
                .posY(entity.getPosY())
                .activo(entity.getActivo())
                .build();
    }

    public LocationJpaEntity toEntity(Location domain) {
        if (domain == null) return null;
        LocationJpaEntity entity = new LocationJpaEntity();
        entity.setIdLocacion(domain.idLocacion());
        entity.setIdAlmacen(domain.idAlmacen());
        entity.setZona(domain.zona());
        entity.setPasillo(domain.pasillo());
        entity.setEstante(domain.estante());
        entity.setCodBarras(domain.codBarras());
        entity.setCapacidad(domain.capacidad());
        entity.setPosX(domain.posX());
        entity.setPosY(domain.posY());
        entity.setActivo(domain.activo());
        return entity;
    }
}
