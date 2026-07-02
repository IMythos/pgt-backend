package com.portable.microservices.ms_tracking.picking.application.usecases;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.portable.microservices.ms_tracking.picking.domain.model.DetallePick;
import com.portable.microservices.ms_tracking.picking.domain.model.RutaPick;
import com.portable.microservices.ms_tracking.picking.domain.ports.in.OptimizarRutaPortIn;
import com.portable.microservices.ms_tracking.picking.domain.ports.out.OrdenPickPersistencePortOut;
import com.portable.microservices.ms_tracking.picking.domain.ports.out.RutaPickPersistencePortOut;
import com.portable.microservices.ms_tracking.picking.domain.service.ConstructorGrafoService;
import com.portable.microservices.ms_tracking.picking.domain.service.OptimizadorRutaService;
import com.portable.microservices.ms_tracking.picking.infrastructure.client.InventoryFeignClient;
import com.portable.microservices.ms_tracking.picking.infrastructure.client.dto.LocationResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptimizarRutaUseCase implements OptimizarRutaPortIn {

    private final OrdenPickPersistencePortOut ordenPickPersistence;
    private final RutaPickPersistencePortOut rutaPickPersistence;
    private final InventoryFeignClient inventoryFeignClient;
    private final ConstructorGrafoService constructorGrafo;
    private final OptimizadorRutaService optimizador;

    @Override
    @Transactional
    public RutaPick execute(UUID idOrden) {
        log.info("=== INICIANDO OPTIMIZACIÓN de ruta para orden {} ===", idOrden);

        var orden = ordenPickPersistence.findById(idOrden)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + idOrden));
        log.debug("Orden encontrada: {} (docRef: {})", orden.idOrden(), orden.docRef());

        log.debug("Paso 1/5: Obteniendo locaciones desde ms-inventory via Feign...");
        var locationResponse = inventoryFeignClient.getAllLocations();
        log.debug("Respuesta de Feign - success: {}, data size: {}", 
                locationResponse != null ? locationResponse.isSuccess() : "null",
                locationResponse != null && locationResponse.getData() != null ? locationResponse.getData().size() : 0);
        
        if (locationResponse == null || locationResponse.getData() == null || locationResponse.getData().isEmpty()) {
            log.error("No se pudieron obtener locaciones desde ms-inventory. Response vacía o nula.");
            throw new RuntimeException("No se pudieron obtener locaciones para optimizar ruta. Response: " + locationResponse);
        }

        List<LocationResponse> allLocations = locationResponse.getData();
        log.debug("Locaciones obtenidas: {} registros", allLocations.size());

        log.debug("Paso 2/5: Obteniendo detalles de la orden...");
        List<UUID> locacionesRecoger = ordenPickPersistence.findDetallesByIdOrden(idOrden)
                .stream()
                .map(DetallePick::locacionId)
                .distinct()
                .toList();
        log.debug("Locaciones a recoger ({}): {}", locacionesRecoger.size(), locacionesRecoger);

        log.debug("Paso 3/5: Validando almacén(es) de las locaciones a recoger...");
        var warehouseIds = allLocations.stream()
                .filter(loc -> locacionesRecoger.contains(loc.getIdLocacion()))
                .map(LocationResponse::getIdAlmacen)
                .distinct()
                .toList();
        
        log.debug("Almacenes encontrados para las locaciones: {}", warehouseIds);
        if (warehouseIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "Ninguna locación a recoger encontrada en el inventario. Locaciones solicitadas: "
                            + locacionesRecoger);
        }

        log.debug("Paso 4-5/5: Optimizando por cada almacén ({} almacén(es))...", warehouseIds.size());
        List<UUID> combinedPath = new ArrayList<>();
        BigDecimal totalDistance = BigDecimal.ZERO;

        for (int i = 0; i < warehouseIds.size(); i++) {
            Long whId = warehouseIds.get(i);
            log.debug("Procesando almacén {}/{}: whId={}", i + 1, warehouseIds.size(), whId);

            List<LocationResponse> locationsInWh = allLocations.stream()
                    .filter(loc -> whId.equals(loc.getIdAlmacen()))
                    .toList();

            List<UUID> pickLocationsInWh = locacionesRecoger.stream()
                    .filter(locId -> allLocations.stream()
                            .anyMatch(l -> l.getIdLocacion().equals(locId) && whId.equals(l.getIdAlmacen())))
                    .toList();

            log.debug("Construyendo grafo con {} locations del whId={}...", locationsInWh.size(), whId);
            var grafo = constructorGrafo.construirGrafo(locationsInWh, null);
            log.debug("Grafo construido: {} nodos, {} aristas", grafo.nodos().size(), grafo.aristas().size());

            log.debug("Ejecutando Dijkstra para {} locaciones en whId={}...", pickLocationsInWh.size(), whId);
            var rutaOptima = optimizador.optimizar(grafo, pickLocationsInWh);
            log.info("Ruta para almacén {}: distancia={}, pathLength={}",
                    whId, rutaOptima.distanciaTotal(), rutaOptima.pathCompleto().size());

            List<UUID> segmentPath = rutaOptima.pathCompleto();
            int segSize = segmentPath.size();

            if (i == 0) {
                combinedPath.addAll(segmentPath.subList(0, segSize - 1));
            } else {
                combinedPath.addAll(segmentPath.subList(1, segSize - 1));
            }
            totalDistance = totalDistance.add(rutaOptima.distanciaTotal());
        }
        combinedPath.add(ConstructorGrafoService.SALIDA_ID);

        log.info("Ruta multi-almacén combinada: distancia={}, pathLength={}", totalDistance, combinedPath.size());
        var ruta = RutaPick.builder()
                .idRuta(UUID.randomUUID())
                .idOrden(idOrden)
                .pathSeq(combinedPath)
                .distanciaEstimada(totalDistance)
                .fecCreacion(OffsetDateTime.now())
                .build();

        var saved = rutaPickPersistence.save(ruta);
        log.info("=== Ruta guardada exitosamente para orden {} (idRuta: {}) ===", idOrden, saved.idRuta());
        return saved;
    }
}