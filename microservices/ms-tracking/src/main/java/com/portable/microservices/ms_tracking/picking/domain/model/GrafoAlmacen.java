package com.portable.microservices.ms_tracking.picking.domain.model;

import java.util.List;

import lombok.Builder;

@Builder(toBuilder = true)
public record GrafoAlmacen(
        List<NodoGrafo> nodos,
        List<AristaGrafo> aristas
) {}