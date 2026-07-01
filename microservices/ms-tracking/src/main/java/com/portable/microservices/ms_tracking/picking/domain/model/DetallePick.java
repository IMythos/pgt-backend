package com.portable.microservices.ms_tracking.picking.domain.model;

import java.util.UUID;

import lombok.Builder;

@Builder(toBuilder = true)
public record DetallePick(
        UUID idDetalle,
        UUID idOrden,
        UUID productoId,
        UUID locacionId,
        Integer cantRequerida,
        Integer cantSeleccion,
        String estado
) {}