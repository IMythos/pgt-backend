package com.portable.microservices.ms_inventory.movement.domain.event;

import java.util.UUID;

public record MovementCreatedEvent(
    UUID movementId,
    UUID productId,
    String tipoMovimiento,
    UUID locacionId,
    Integer cantidad,
    Long userId
) {}