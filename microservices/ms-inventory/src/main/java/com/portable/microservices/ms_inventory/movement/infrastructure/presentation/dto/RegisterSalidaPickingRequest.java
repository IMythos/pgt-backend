package com.portable.microservices.ms_inventory.movement.infrastructure.presentation.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegisterSalidaPickingRequest(
    @NotEmpty @Valid List<SalidaPickingItem> items,
    @NotBlank String motivo,
    String documentoRef
) {
    public record SalidaPickingItem(
        @NotNull UUID idLote,
        @NotNull @Positive Integer cantidad
    ) {}
}
