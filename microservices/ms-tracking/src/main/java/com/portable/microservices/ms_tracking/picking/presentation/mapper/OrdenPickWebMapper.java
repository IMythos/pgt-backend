package com.portable.microservices.ms_tracking.picking.presentation.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.portable.microservices.ms_tracking.picking.domain.model.DetallePick;
import com.portable.microservices.ms_tracking.picking.domain.model.OrdenPick;
import com.portable.microservices.ms_tracking.picking.presentation.dto.CrearOrdenPickRequest;
import com.portable.microservices.ms_tracking.picking.presentation.dto.OrdenPickResponse;

@Mapper(componentModel = "spring")
public interface OrdenPickWebMapper {

    OrdenPickResponse toResponse(OrdenPick orden);

    default List<DetallePick> toDomain(List<CrearOrdenPickRequest.ItemRequest> items) {
        if (items == null) return List.of();
        return items.stream().map(item -> DetallePick.builder()
                .productoId(item.getProductoId())
                .locacionId(item.getLocacionId())
                .cantRequerida(item.getCantRequerida())
                .cantSeleccion(0)
                .estado("PENDIENTE")
                .build()
        ).toList();
    }
}