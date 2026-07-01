package com.portable.microservices.ms_tracking.picking.presentation.dto;

import java.util.UUID;

public record RutaNodeResponse(
        UUID id,
        String tipo,
        String label,
        String zona,
        String pasillo,
        String estante,
        Integer x,
        Integer y
) {}
