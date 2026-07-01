package com.portable.microservices.ms_tracking.heatmap.infrastructure.messaging.dto;

import java.util.UUID;

public record MovementCreatedMessage(
    UUID movementId,
    UUID productId,
    String tipoMovimiento,
    UUID locacionId,
    Integer cantidad,
    Long userId
) {}