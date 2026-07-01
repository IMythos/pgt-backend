package com.portable.microservices.ms_inventory.movement.application.usecases;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.portable.microservices.ms_inventory.kardex.infrastructure.persistence.entity.KardexJpaEntity;
import com.portable.microservices.ms_inventory.lot.infrastructure.persistence.entity.LoteJpaEntity;
import com.portable.microservices.ms_inventory.movement.domain.model.Movimiento;
import com.portable.microservices.ms_inventory.movement.domain.event.MovementCreatedEvent;
import com.portable.microservices.ms_inventory.movement.domain.ports.in.RegisterSalidaPortIn;
import com.portable.microservices.ms_inventory.movement.domain.ports.out.KardexPersistencePortOut;
import com.portable.microservices.ms_inventory.movement.domain.ports.out.LotePersistencePortOut;
import com.portable.microservices.ms_inventory.movement.domain.ports.out.MovimientoPersistencePortOut;
import com.portable.microservices.ms_inventory.shared.domain.event.StockDecreasedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Caso de uso para registrar una salida de inventario.
 * 
 * Crea un registro de movimiento tipo SALIDA y genera un kardex con la cantidad
 * egresada,
 * validando que exista suficiente stock disponible a nivel global para el
 * producto.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterSalidaUseCase implements RegisterSalidaPortIn {

    private final MovimientoPersistencePortOut movimientoPersistence;
    private final KardexPersistencePortOut kardexPersistence;
    private final LotePersistencePortOut lotePersistence;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Movimiento execute(UUID idLote, Long idUsuario, Integer cantidad, String motivo, String docRef) {
        log.info("Iniciando registro de salida para lote: {}, cantidad: {}, usuario: {}", idLote, cantidad, idUsuario);

        LoteJpaEntity lote = lotePersistence.findLoteById(idLote)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado: " + idLote));

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de salida debe ser mayor a 0");
        }

        UUID idProducto = lote.getProducto().getId_producto();

        Optional<KardexJpaEntity> ultimoKardex = kardexPersistence.findLastByProductId(idProducto);
        Integer stockDisponible = ultimoKardex.map(KardexJpaEntity::getStockActual).orElse(0);

        if (stockDisponible < cantidad) {
            throw new IllegalArgumentException(
                    "Stock insuficiente. Disponible: " + stockDisponible + ", solicitado: " + cantidad);
        }

        Movimiento movimiento = Movimiento.crearSalida(idLote, idUsuario, cantidad,
                motivo != null ? motivo : "Salida registrada", docRef);

        if (!movimiento.isValidForCreation()) {
            throw new IllegalArgumentException("Datos inválidos para crear movimiento de salida");
        }

        Movimiento movimientoGuardado = movimientoPersistence.save(movimiento);

        BigDecimal costoPromedio = ultimoKardex.map(KardexJpaEntity::getCostoProm).orElse(BigDecimal.ZERO);
        kardexPersistence.registrarSalida(
                movimientoGuardado.idMovimiento(), idProducto, cantidad, stockDisponible, costoPromedio);

        // Descontar cantidad del lote especificado (FIFO desde el lote dado)
        int remaining = cantidad;
        int fromThisLot = Math.min(remaining, lote.getCantidad());
        lote.setCantidad(lote.getCantidad() - fromThisLot);
        if (lote.getCantidad() == 0) {
            lote.setEstado("AGOTADO");
        }
        lotePersistence.update(lote);
        remaining -= fromThisLot;

        if (remaining > 0) {
            List<LoteJpaEntity> otherLots = lotePersistence.findLotesByProductId(idProducto);
            for (LoteJpaEntity other : otherLots) {
                if (remaining <= 0) break;
                if (other.getIdLote().equals(idLote)) continue;
                if (other.getCantidad() <= 0) continue;

                int deduct = Math.min(remaining, other.getCantidad());
                other.setCantidad(other.getCantidad() - deduct);
                if (other.getCantidad() == 0) {
                    other.setEstado("AGOTADO");
                }
                lotePersistence.update(other);
                remaining -= deduct;
            }
            if (remaining > 0) {
                throw new IllegalArgumentException(
                        "Stock insuficiente en lotes. Faltan " + remaining + " unidades");
            }
        }

        UUID locacionId = lote.getLocacion() != null ? lote.getLocacion().getIdLocacion() : null;
        eventPublisher.publishEvent(new MovementCreatedEvent(
                movimientoGuardado.idMovimiento(),
                idProducto,
                movimientoGuardado.tipo(),
                locacionId,
                movimientoGuardado.cantidad(),
                idUsuario
        ));

        Integer nuevoStock = stockDisponible - cantidad;
        eventPublisher.publishEvent(new StockDecreasedEvent(idProducto, nuevoStock));

        return movimientoGuardado;
    }
}