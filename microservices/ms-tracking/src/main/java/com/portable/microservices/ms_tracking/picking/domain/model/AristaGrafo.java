package com.portable.microservices.ms_tracking.picking.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;

@Builder(toBuilder = true)
public record AristaGrafo(
        UUID idNodoOrigen,
        UUID idNodoDestino,
        BigDecimal peso
) {}