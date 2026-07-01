package com.portable.microservices.ms_inventory.locations.presentation.dto;

public record UpdateLocationRequest(

    String zona,
    String pasillo,
    String estante,
    Integer capacidad,
    Integer posX,
    Integer posY
) {
}
