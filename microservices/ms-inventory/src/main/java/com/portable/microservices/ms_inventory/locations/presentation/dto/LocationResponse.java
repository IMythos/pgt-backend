package com.portable.microservices.ms_inventory.locations.presentation.dto;

import java.util.UUID;

public record LocationResponse(
    UUID idLocacion,
    Long idAlmacen,
    String zona,
    String pasillo,
    String estante,
    String codBarras,
    Integer capacidad,
    Integer posX,
    Integer posY,
    Boolean activo
) {
}
