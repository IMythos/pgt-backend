package com.portable.microservices.ms_tracking.picking.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder(toBuilder = true)
public record AuditoriaPick(
        UUID idAuditoria,
        OffsetDateTime fechaEvento,
        Long idUsuario,
        String entidadTab,
        String entidadId,
        String accion,
        String ipOrigen,
        String msOrigen,
        String valoresAnteriores,
        String valoresNuevos
) {}