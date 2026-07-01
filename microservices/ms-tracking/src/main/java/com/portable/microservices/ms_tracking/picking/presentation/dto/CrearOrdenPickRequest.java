package com.portable.microservices.ms_tracking.picking.presentation.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearOrdenPickRequest {

    @NotNull
    private Long usuarioCreador;

    @NotEmpty
    private List<ItemRequest> items;

    private String tipoSalida = "SALIDA";

    private String docRef;

    @Data
    public static class ItemRequest {
        @NotNull
        private UUID productoId;

        @NotNull
        private UUID locacionId;

        @NotNull
        private Integer cantRequerida;
    }
}