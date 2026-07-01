package com.portable.microservices.ms_tracking.picking.application.usecases;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.portable.microservices.ms_tracking.picking.domain.model.AuditoriaPick;
import com.portable.microservices.ms_tracking.picking.domain.model.OrdenPick;
import com.portable.microservices.ms_tracking.picking.domain.ports.in.CompletarOrdenPickPortIn;
import com.portable.microservices.ms_tracking.picking.domain.ports.out.AuditoriaPickPersistencePortOut;
import com.portable.microservices.ms_tracking.picking.domain.ports.out.OrdenPickPersistencePortOut;
import com.portable.microservices.ms_tracking.picking.infrastructure.messaging.dto.PickingCompletedMessage;
import com.portable.microservices.ms_tracking.picking.infrastructure.messaging.publisher.PickingEventPublisher;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompletarOrdenPickUseCase implements CompletarOrdenPickPortIn {
        private final OrdenPickPersistencePortOut ordenPickPersistence;
        private final AuditoriaPickPersistencePortOut auditoriaPersistence;
        private final PickingEventPublisher eventPublisher;

        @Override
        @Transactional
        public OrdenPick execute(UUID idOrden, Long usuarioId, String ipOrigen) {
                var orden = ordenPickPersistence.findById(idOrden)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Orden de picking no encontrada: " + idOrden));

                if (!"EN_PROCESO".equals(orden.estado())) {
                        throw new IllegalStateException("Solo se pueden completar órdenes en estado EN_PROCESO");
                }

                var ordenCompletada = orden.toBuilder()
                                .estado("COMPLETADO")
                                .fecFin(OffsetDateTime.now())
                                .build();

                ordenPickPersistence.save(ordenCompletada);

                var detalles = ordenPickPersistence.findDetallesByIdOrden(idOrden);

                auditoriaPersistence.save(AuditoriaPick.builder()
                                .idAuditoria(UUID.randomUUID())
                                .fechaEvento(OffsetDateTime.now())
                                .idUsuario(usuarioId)
                                .entidadTab("orden_pick")
                                .entidadId(idOrden.toString())
                                .accion("CIERRE_ORDEN")
                                .ipOrigen(ipOrigen)
                                .msOrigen("ms-tracking")
                                .build());

                var itemsMessage = detalles.stream()
                                .map(d -> new PickingCompletedMessage.ItemRecogido(
                                                d.productoId(), d.locacionId(), d.cantSeleccion()))
                                .toList();

                eventPublisher.publish(new PickingCompletedMessage(
                                idOrden,
                                usuarioId,
                                itemsMessage));

                return ordenCompletada;
        }
}
