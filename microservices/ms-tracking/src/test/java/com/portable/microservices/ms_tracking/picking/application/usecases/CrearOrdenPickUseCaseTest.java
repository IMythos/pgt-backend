package com.portable.microservices.ms_tracking.picking.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.portable.microservices.ms_tracking.picking.domain.model.DetallePick;
import com.portable.microservices.ms_tracking.picking.domain.model.OrdenPick;
import com.portable.microservices.ms_tracking.picking.domain.ports.out.OrdenPickPersistencePortOut;

@ExtendWith(MockitoExtension.class)
class CrearOrdenPickUseCaseTest {
    @Mock
    private OrdenPickPersistencePortOut ordenPickPersistence;
    @InjectMocks
    private CrearOrdenPickUseCase useCase;

    @Test
    void executeDebeCrearOrdenPendienteYGuardarDetalles() {
        UUID ordenId = UUID.randomUUID();
        DetallePick item = DetallePick.builder()
                .productoId(UUID.randomUUID())
                .locacionId(UUID.randomUUID())
                .cantRequerida(2)
                .cantSeleccion(0)
                .estado("PENDIENTE")
                .build();
        OrdenPick guardada = OrdenPick.builder()
                .idOrden(ordenId)
                .usuarioPickingId(7L)
                .estado("PENDIENTE")
                .tipoSalida("SALIDA")
                .docRef("DOC-10")
                .build();
        when(ordenPickPersistence.save(any(OrdenPick.class))).thenReturn(guardada);
        when(ordenPickPersistence.save(any(DetallePick.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdenPick result = useCase.execute(7L, List.of(item), null, "DOC-10");

        assertEquals(ordenId, result.idOrden());
        assertEquals("PENDIENTE", result.estado());
        verify(ordenPickPersistence).save(any(OrdenPick.class));
        verify(ordenPickPersistence).save(any(DetallePick.class));
    }

    @Test
    void executeDebeRechazarOrdenSinItems() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(7L, List.of(), "SALIDA", "DOC-10"));

        verify(ordenPickPersistence, never()).save(any(OrdenPick.class));
    }
}
