package com.portable.microservices.ms_tracking.picking.infrastructure.messaging.listener;

import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.portable.microservices.ms_tracking.heatmap.infrastructure.messaging.dto.MovementCreatedMessage;
import com.portable.microservices.ms_tracking.picking.domain.model.DetallePick;
import com.portable.microservices.ms_tracking.picking.domain.ports.in.CrearOrdenPickPortIn;
import com.portable.microservices.ms_tracking.picking.domain.ports.in.OptimizarRutaPortIn;
import com.portable.microservices.ms_tracking.picking.infrastructure.client.InventoryFeignClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PickingFromSalidaEventListener {

    private final CrearOrdenPickPortIn crearOrdenPick;
    private final OptimizarRutaPortIn optimizarRuta;
    private final InventoryFeignClient inventoryFeignClient;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "tracking.picking.salida.queue", durable = "true"), exchange = @Exchange(value = "inventory.exchange", type = "topic"), key = "inventory.movement.created"))
    public void handleSalidaCreada(MovementCreatedMessage message) {
        if (!"SALIDA".equals(message.tipoMovimiento()) && !"AJUSTE_NEGATIVO".equals(message.tipoMovimiento())) {
            return;
        }

        String docRef = message.movementId().toString();
        log.info("Movimiento tipo {} detectado, creando orden de picking. Movimiento ID: {}, Usuario: {}, Locacion: {}",
                message.tipoMovimiento(), docRef, message.userId(), message.locacionId());

        UUID locacionId = message.locacionId();

        if (locacionId == null) {
            log.warn("{} {} sin locación, buscando ubicación alternativa...", message.tipoMovimiento(), docRef);
            try {
                var locationResponse = inventoryFeignClient.getAllLocations();
                if (locationResponse != null && locationResponse.getData() != null && !locationResponse.getData().isEmpty()) {
                    var fallbackLoc = locationResponse.getData().stream()
                            .filter(loc -> Boolean.TRUE.equals(loc.getActivo()))
                            .findFirst()
                            .orElse(locationResponse.getData().get(0));
                    locacionId = fallbackLoc.getIdLocacion();
                    log.info("Usando ubicación alternativa {} para la orden de picking.", locacionId);
                } else {
                    log.warn("Respuesta de locaciones vacía o nula: response={}", locationResponse);
                }
            } catch (Exception e) {
                log.error("Error al obtener ubicaciones alternativas desde FeignClient", e);
            }
            if (locacionId == null) {
                log.warn("No se encontró ubicación alternativa para {}, se omite creación de picking.", docRef);
                return;
            }
        }

        var item = DetallePick.builder()
                .productoId(message.productId())
                .locacionId(locacionId)
                .cantRequerida(message.cantidad())
                .cantSeleccion(0)
                .estado("PENDIENTE")
                .build();

        // Buscar si ya existe orden para este movimiento (idempotencia)
        var ordenExistente = crearOrdenPick.buscarPorDocRef(docRef);
        if (ordenExistente.isPresent()) {
            UUID idOrden = ordenExistente.get().idOrden();
            log.info("Orden de picking {} ya existe para movimiento {}, re-optimizando ruta...", idOrden, docRef);
            optimizarRuta.execute(idOrden);
            log.info("Ruta re-optimizada para orden {}.", idOrden);
            return;
        }

        try {
            var orden = crearOrdenPick.execute(
                    message.userId() != null ? message.userId() : 0L,
                    List.of(item),
                    message.tipoMovimiento(),
                    docRef);
            log.info("Orden de picking {} creada desde SALIDA {}.", orden.idOrden(), docRef);
            optimizarRuta.execute(orden.idOrden());
            log.info("Ruta optimizada para orden {}.", orden.idOrden());
        } catch (Exception e) {
            log.error("Error fatal en flujo de picking desde SALIDA {} (orden creada: {})", docRef, ordenExistente.isPresent(), e);
            throw new RuntimeException("Error en flujo de picking para SALIDA " + docRef, e);
        }
    }
}
