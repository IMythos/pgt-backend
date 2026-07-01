package com.portable.microservices.ms_inventory.movement.domain.ports.in;

import java.util.List;
import java.util.UUID;

public interface RegisterSalidaBatchPortIn {
    List<UUID> execute(List<SalidaBatchItem> items, String motivo, String documentoRef, Long userId);

    record SalidaBatchItem(UUID idLote, Integer cantidad) {}
}
