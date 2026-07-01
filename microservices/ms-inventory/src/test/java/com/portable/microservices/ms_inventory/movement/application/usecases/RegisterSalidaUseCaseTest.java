package com.portable.microservices.ms_inventory.movement.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.portable.microservices.ms_inventory.kardex.infrastructure.persistence.entity.KardexJpaEntity;
import com.portable.microservices.ms_inventory.lot.infrastructure.persistence.entity.LoteJpaEntity;
import com.portable.microservices.ms_inventory.movement.domain.model.Movimiento;
import com.portable.microservices.ms_inventory.movement.domain.ports.out.KardexPersistencePortOut;
import com.portable.microservices.ms_inventory.movement.domain.ports.out.LotePersistencePortOut;
import com.portable.microservices.ms_inventory.movement.domain.ports.out.MovimientoPersistencePortOut;
import com.portable.microservices.ms_inventory.product.infrastructure.persistence.entity.ProductJpaEntity;

@ExtendWith(MockitoExtension.class)
class RegisterSalidaUseCaseTest {
    @Mock
    private MovimientoPersistencePortOut movimientoPersistence;
    @Mock
    private KardexPersistencePortOut kardexPersistence;
    @Mock
    private LotePersistencePortOut lotePersistence;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private RegisterSalidaUseCase useCase;

    @Test
    void executeDebeRegistrarSalidaCuandoHayStockDisponible() {
        UUID loteId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        LoteJpaEntity lote = lote(loteId, productId, 8);
        KardexJpaEntity ultimoKardex = kardex(10, new BigDecimal("7.5000"));
        UUID movementId = UUID.randomUUID();

        when(lotePersistence.findLoteById(loteId)).thenReturn(Optional.of(lote));
        when(kardexPersistence.findLastByProductId(productId)).thenReturn(Optional.of(ultimoKardex));
        when(movimientoPersistence.save(any(Movimiento.class)))
                .thenReturn(new Movimiento(movementId, loteId, 3L, "SALIDA", 4, null, "Venta", "DOC-1"));

        Movimiento result = useCase.execute(loteId, 3L, 4, "Venta", "DOC-1");

        assertEquals(movementId, result.idMovimiento());
        assertEquals(4, lote.getCantidad());
        verify(kardexPersistence).registrarSalida(movementId, productId, 4, 10, new BigDecimal("7.5000"));
        verify(lotePersistence).update(lote);
        verify(eventPublisher, times(2)).publishEvent(any(Object.class));
    }

    @Test
    void executeDebeRechazarSalidaCuandoStockEsInsuficiente() {
        UUID loteId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        when(lotePersistence.findLoteById(loteId)).thenReturn(Optional.of(lote(loteId, productId, 2)));
        when(kardexPersistence.findLastByProductId(productId)).thenReturn(Optional.of(kardex(2, BigDecimal.ONE)));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(loteId, 3L, 5, "Venta", "DOC-1"));

        verify(movimientoPersistence, never()).save(any());
        verify(kardexPersistence, never()).registrarSalida(any(), any(), any(), any(), any());
    }

    @Test
    void executeDebeRechazarCantidadNoPositiva() {
        UUID loteId = UUID.randomUUID();
        when(lotePersistence.findLoteById(loteId)).thenReturn(Optional.of(lote(loteId, UUID.randomUUID(), 2)));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(loteId, 3L, 0, "Venta", "DOC-1"));

        verify(movimientoPersistence, never()).save(any());
    }

    private LoteJpaEntity lote(UUID loteId, UUID productId, int cantidad) {
        ProductJpaEntity product = ProductJpaEntity.builder().id_producto(productId).build();
        LoteJpaEntity lote = new LoteJpaEntity();
        lote.setIdLote(loteId);
        lote.setProducto(product);
        lote.setCantidad(cantidad);
        lote.setCostoUnit(BigDecimal.TEN);
        return lote;
    }

    private KardexJpaEntity kardex(int stockActual, BigDecimal costoProm) {
        KardexJpaEntity kardex = new KardexJpaEntity();
        kardex.setStockActual(stockActual);
        kardex.setCostoProm(costoProm);
        return kardex;
    }
}
