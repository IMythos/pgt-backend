package com.portable.microservices.ms_tracking.picking.domain.ports.in;

import java.util.UUID;

import com.portable.microservices.ms_tracking.picking.domain.model.RutaPick;

public interface OptimizarRutaPortIn {
    RutaPick execute(UUID idOrden);
}