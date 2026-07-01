package com.portable.microservices.ms_tracking.picking.application.usecases;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.portable.microservices.ms_tracking.picking.domain.model.DetallePick;
import com.portable.microservices.ms_tracking.picking.domain.model.OrdenPick;
import com.portable.microservices.ms_tracking.picking.domain.ports.in.CrearOrdenPickPortIn;
import com.portable.microservices.ms_tracking.picking.domain.ports.out.OrdenPickPersistencePortOut;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CrearOrdenPickUseCase implements CrearOrdenPickPortIn {
    private final OrdenPickPersistencePortOut ordenPickPersistence;
    @Override
    @Transactional
    public OrdenPick execute(Long usuarioCreador, List<DetallePick> items, String tipoSalida, String docRef) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La orden debe tener al menos un item");
        }

        OrdenPick orden = OrdenPick.builder()
                .idOrden(UUID.randomUUID())
                .usuarioPickingId(usuarioCreador)
                .estado("PENDIENTE")
                .fecCreacion(OffsetDateTime.now())
                .tipoSalida(tipoSalida != null ? tipoSalida : "SALIDA")
                .docRef(docRef)
                .build();

        var ordenGuardada = ordenPickPersistence.save(orden);
         for (var item : items) {
            var itemConOrden = item.toBuilder()
                    .idDetalle(UUID.randomUUID())
                    .idOrden(ordenGuardada.idOrden())
                    .build();
            ordenPickPersistence.save(itemConOrden);
        }

        return ordenGuardada;
    }

    @Override
    @Transactional
    public Optional<OrdenPick> buscarPorDocRef(String docRef) {
        return ordenPickPersistence.findByDocRef(docRef);
    }
    
}
