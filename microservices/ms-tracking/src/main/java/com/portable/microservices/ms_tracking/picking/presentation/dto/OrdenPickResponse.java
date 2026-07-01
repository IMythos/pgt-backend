package com.portable.microservices.ms_tracking.picking.presentation.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrdenPickResponse {
    private UUID idOrden;
    private Long usuarioPickingId;
    private String estado;
    private OffsetDateTime fecCreacion;
    private OffsetDateTime fecInicio;
    private OffsetDateTime fecFin;
    private String tipoSalida;
    private String docRef;
}
