package com.portable.microservices.ms_tracking.picking.presentation.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearOrdenDesdeSalidaRequest {
    @NotEmpty @Valid
    private List<Item> items;

    @NotBlank
    private String motivo;

    private String docRef;

    @NotNull
    private Long usuarioCreador;

    @Data
    public static class Item {
        @NotNull
        private UUID productoId;

        @NotNull
        private UUID locacionId;

        @NotNull
        private UUID idLote;

        @NotNull
        private Integer cantidad;
    }
}
