package com.portable.microservices.ms_tracking.picking.presentation.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record RutaResponse(
        List<UUID> pathSeq,
        List<RutaNodeResponse> nodes,
        List<UUID> pickingStops,
        BigDecimal distanciaEstimada,
        List<DetallePickResponse> detalles
) {}
