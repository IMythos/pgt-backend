package com.portable.microservices.ms_tracking.picking.infrastructure.messaging.dto;

import java.util.List;
import java.util.UUID;

public record PickingCompletedMessage(
        UUID ordenId,
        Long usuarioId,
        List<ItemRecogido> items
) {
    public record ItemRecogido(
            UUID productoId,
            UUID locacionId,
            Integer cantidadRealRecogida
    ) {}
}