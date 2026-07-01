package com.portable.microservices.ms_tracking.picking.domain.ports.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.portable.microservices.ms_tracking.picking.domain.model.DetallePick;
import com.portable.microservices.ms_tracking.picking.domain.model.OrdenPick;

public interface OrdenPickPersistencePortOut {
    OrdenPick save(OrdenPick orden);
    Optional<OrdenPick> findById(UUID idOrden);
    Optional<OrdenPick> findByDocRef(String docRef);
    List<OrdenPick> findAll();
    List<OrdenPick> findByEstado(String estado);
    DetallePick save(DetallePick detalle);
    List<DetallePick> findDetallesByIdOrden(UUID idOrden);
}