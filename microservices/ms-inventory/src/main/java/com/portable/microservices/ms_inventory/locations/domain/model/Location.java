package com.portable.microservices.ms_inventory.locations.domain.model;

import java.util.UUID;

import lombok.Builder;

@Builder(toBuilder = true)
public record Location(
    UUID idLocacion,
    Long idAlmacen, // FK hacia warehouse
    String zona,
    String pasillo,
    String estante,
    String codBarras,
    Integer capacidad,
    Integer posX,
    Integer posY,
    Boolean activo
) {
    public boolean isValidForCreation() {
        return idAlmacen != null && codBarras != null && !codBarras.isBlank();
    }
}
