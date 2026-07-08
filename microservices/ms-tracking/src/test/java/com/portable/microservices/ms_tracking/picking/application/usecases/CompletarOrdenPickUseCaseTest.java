package com.portable.microservices.ms_tracking.picking.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.portable.microservices.ms_tracking.picking.domain.model.AuditoriaPick;
import com.portable.microservices.ms_tracking.picking.domain.model.DetallePick;
import com.portable.microservices.ms_tracking.picking.domain.model.OrdenPick;
import com.portable.microservices.ms_tracking.picking.domain.ports.out.AuditoriaPickPersistencePortOut;
import com.portable.microservices.ms_tracking.picking.domain.ports.out.OrdenPickPersistencePortOut;
import com.portable.microservices.ms_tracking.picking.infrastructure.messaging.dto.PickingCompletedMessage;
import com.portable.microservices.ms_tracking.picking.infrastructure.messaging.publisher.PickingEventPublisher;

@ExtendWith(MockitoExtension.class)
class CompletarOrdenPickUseCaseTest {
    @Mock
    private OrdenPickPersistencePortOut ordenPickPersistence;
    @Mock
    private AuditoriaPickPersistencePortOut auditoriaPersistence;
    @Mock
    private PickingEventPublisher eventPublisher;
    @InjectMocks
    private CompletarOrdenPickUseCase useCase;

    @Test
    void executeDebeCompletarOrdenEnProcesoYPublicarEvento() {
        UUID ordenId = UUID.randomUUID();
        OrdenPick orden = OrdenPick.builder().idOrden(ordenId).estado("EN_PROCESO").build();
        DetallePick detalle = DetallePick.builder()
                .productoId(UUID.randomUUID())
                .locacionId(UUID.randomUUID())
                .cantSeleccion(2)
                .build();
        when(ordenPickPersistence.findById(ordenId)).thenReturn(Optional.of(orden));
        when(ordenPickPersistence.findDetallesByIdOrden(ordenId)).thenReturn(List.of(detalle));

        OrdenPick result = useCase.execute(ordenId, 15L, "127.0.0.1");

        assertEquals("COMPLETADO", result.estado());
        verify(ordenPickPersistence).save(result);
        verify(auditoriaPersistence).save(any(AuditoriaPick.class));
        verify(eventPublisher).publish(any(PickingCompletedMessage.class));
    }

    @Test
    void executeDebeRechazarOrdenQueNoEstaEnProceso() {
        UUID ordenId = UUID.randomUUID();
        when(ordenPickPersistence.findById(ordenId))
                .thenReturn(Optional.of(OrdenPick.builder().idOrden(ordenId).estado("PENDIENTE").build()));

        assertThrows(IllegalStateException.class, () -> useCase.execute(ordenId, 15L, "127.0.0.1"));

        verify(ordenPickPersistence, never()).save(any(OrdenPick.class));
        verify(eventPublisher, never()).publish(any());
    }
}
