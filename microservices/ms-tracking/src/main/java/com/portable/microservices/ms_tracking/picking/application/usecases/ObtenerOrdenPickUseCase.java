package com.portable.microservices.ms_tracking.picking.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.portable.microservices.ms_tracking.picking.domain.model.OrdenPick;
import com.portable.microservices.ms_tracking.picking.domain.ports.in.ObtenerOrdenPickPortIn;
import com.portable.microservices.ms_tracking.picking.domain.ports.out.OrdenPickPersistencePortOut;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ObtenerOrdenPickUseCase implements ObtenerOrdenPickPortIn {
    private final OrdenPickPersistencePortOut ordenPickPersistence;

    @Override
    public OrdenPick execute(UUID idOrden) {
        return ordenPickPersistence.findById(idOrden)
                .orElseThrow(() -> new IllegalArgumentException("Orden de picking no encontrada: " + idOrden));
    }

}
