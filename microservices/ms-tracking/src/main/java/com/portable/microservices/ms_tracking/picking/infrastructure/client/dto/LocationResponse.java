package com.portable.microservices.ms_tracking.picking.infrastructure.client.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class LocationResponse {
    private UUID idLocacion;
    private Long idAlmacen;
    private String zona;
    private String pasillo;
    private String estante;
    private String codBarras;
    private Integer capacidad;
    private Integer posX;
    private Integer posY;
    private Boolean activo;
}
