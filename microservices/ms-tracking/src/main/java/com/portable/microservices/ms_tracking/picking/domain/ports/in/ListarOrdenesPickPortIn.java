package com.portable.microservices.ms_tracking.picking.domain.ports.in;

import java.util.List;

import com.portable.microservices.ms_tracking.picking.domain.model.OrdenPick;

public interface ListarOrdenesPickPortIn {
    List<OrdenPick> execute();
    List<OrdenPick> executeByEstado(String estado);
}