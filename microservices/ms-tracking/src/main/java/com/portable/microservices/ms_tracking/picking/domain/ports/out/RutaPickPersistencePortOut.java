package com.portable.microservices.ms_tracking.picking.domain.ports.out;

import java.util.Optional;
import java.util.UUID;

import com.portable.microservices.ms_tracking.picking.domain.model.RutaPick;

public interface RutaPickPersistencePortOut {
    RutaPick save(RutaPick ruta);
    Optional<RutaPick> findByIdOrden(UUID idOrden);
}