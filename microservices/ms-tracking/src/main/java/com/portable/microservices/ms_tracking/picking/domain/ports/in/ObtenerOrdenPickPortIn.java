package com.portable.microservices.ms_tracking.picking.domain.ports.in;

import java.util.UUID;

import com.portable.microservices.ms_tracking.picking.domain.model.OrdenPick;

public interface ObtenerOrdenPickPortIn {
    OrdenPick execute(UUID idOrden);
}