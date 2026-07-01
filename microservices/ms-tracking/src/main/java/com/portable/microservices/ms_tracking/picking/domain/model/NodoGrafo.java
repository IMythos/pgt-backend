package com.portable.microservices.ms_tracking.picking.domain.model;

import java.util.UUID;

import lombok.Builder;

@Builder(toBuilder = true)
public record NodoGrafo(
        UUID idNodo,
        String zona,
        String pasillo,
        String estante,
        String tipoNodo,
        Integer posX,
        Integer posY
) {}