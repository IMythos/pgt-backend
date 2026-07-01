package com.portable.microservices.ms_inventory.locations.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateLocationRequest(
    @NotNull(message = "El ID de almacén es obligatorio")
    Long idAlmacen,

    String zona,
    String pasillo,
    String estante,

    @NotBlank(message = "El código de barras es obligatorio")
    String codBarras,

    Integer capacidad,
    Integer posX,
    Integer posY
) {
}
