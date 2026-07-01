package com.portable.microservices.ms_tracking.picking.infrastructure.persistence.mapper;

import com.portable.microservices.ms_tracking.picking.domain.model.DetallePick;
import com.portable.microservices.ms_tracking.picking.infrastructure.persistence.entity.DetallePickJpaEntity;
import com.portable.microservices.ms_tracking.picking.infrastructure.persistence.entity.OrdenPickJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DetallePickPersistenceMapper {

    @Mapping(target = "orden", source = "idOrden", qualifiedByName = "ordenFromId")
    DetallePickJpaEntity toEntity(DetallePick domain);

    @Mapping(target = "idOrden", source = "orden.idOrden")
    DetallePick toDomain(DetallePickJpaEntity entity);

    @Named("ordenFromId")
    default OrdenPickJpaEntity ordenFromId(java.util.UUID idOrden) {
        if (idOrden == null) return null;
        var ref = new OrdenPickJpaEntity();
        ref.setIdOrden(idOrden);
        return ref;
    }
}
