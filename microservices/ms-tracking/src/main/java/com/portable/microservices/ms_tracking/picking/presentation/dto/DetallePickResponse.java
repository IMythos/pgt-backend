package com.portable.microservices.ms_tracking.picking.presentation.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetallePickResponse {
    private UUID idDetalle;
    private UUID productoId;
    private UUID locacionId;
    private Integer cantRequerida;
    private Integer cantSeleccion;
    private String estado;
}
