package com.portable.microservices.ms_tracking.picking.domain.ports.in;

import java.util.List;
import java.util.Optional;

import com.portable.microservices.ms_tracking.picking.domain.model.DetallePick;
import com.portable.microservices.ms_tracking.picking.domain.model.OrdenPick;

public interface CrearOrdenPickPortIn {
    OrdenPick execute(Long usuarioCreador, List<DetallePick> items, String tipoSalida, String docRef);
    Optional<OrdenPick> buscarPorDocRef(String docRef);
}