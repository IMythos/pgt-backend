package com.portable.microservices.ms_tracking.picking.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;

@Builder(toBuilder = true)
public record RutaPick(
        UUID idRuta,
        UUID idOrden,
        List<UUID> pathSeq,
        BigDecimal distanciaEstimada,
        OffsetDateTime fecCreacion
) {}