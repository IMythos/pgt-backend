package com.portable.microservices.ms_tracking.picking.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder(toBuilder = true)
public record OrdenPick(
        UUID idOrden,
        Long usuarioPickingId,
        String estado,
        OffsetDateTime fecCreacion,
        OffsetDateTime fecInicio,
        OffsetDateTime fecFin,
        String tipoSalida,
        String docRef
) {}