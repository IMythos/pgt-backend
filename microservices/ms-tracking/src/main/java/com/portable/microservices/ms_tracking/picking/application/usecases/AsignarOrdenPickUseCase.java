package com.portable.microservices.ms_tracking.picking.application.usecases;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.portable.microservices.ms_tracking.picking.domain.model.OrdenPick;
import com.portable.microservices.ms_tracking.picking.domain.ports.in.AsignarOrdenPickPortIn;
import com.portable.microservices.ms_tracking.picking.domain.ports.out.OrdenPickPersistencePortOut;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AsignarOrdenPickUseCase implements AsignarOrdenPickPortIn {

     private final OrdenPickPersistencePortOut ordenPickPersistence;
    @Override
    @Transactional
    public OrdenPick execute(UUID idOrden, Long usuarioPickingId) {
        var orden = ordenPickPersistence.findById(idOrden)
                .orElseThrow(() -> new IllegalArgumentException("Orden de picking no encontrada: " + idOrden));

        if (!"PENDIENTE".equals(orden.estado())) {
            throw new IllegalStateException("Solo se pueden asignar órdenes en estado PENDIENTE");
        }

        var ordenActualizada = orden.toBuilder()
                .usuarioPickingId(usuarioPickingId)
                .estado("EN_PROCESO")
                .fecInicio(OffsetDateTime.now())
                .build();

        return ordenPickPersistence.save(ordenActualizada);
    }
}
