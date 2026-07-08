package com.portable.microservices.ms_tracking.picking.application.usecases;

import java.time.OffsetDateTime;
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
        if (warehouseIds.size() > 1) {
            throw new IllegalArgumentException(
                    "Las locaciones de la orden pertenecen a múltiples almacenes: " + warehouseIds);
        }
        Long warehouseId = warehouseIds.get(0);
        log.debug("Almacén ID único: {}", warehouseId);

        List<LocationResponse> locationsInWarehouse = allLocations.stream()
                .filter(loc -> warehouseId.equals(loc.getIdAlmacen()))
                .toList();

        log.debug("Paso 4/5: Construyendo grafo con {} locations del warehouseId={}...", locationsInWarehouse.size(), warehouseId);
        var grafo = constructorGrafo.construirGrafo(locationsInWarehouse, null);
        log.debug("Grafo construido: {} nodos, {} aristas", grafo.nodos().size(), grafo.aristas().size());

        log.debug("Paso 5/5: Ejecutando optimizador Dijkstra para {} locaciones...", locacionesRecoger.size());
        var rutaOptima = optimizador.optimizar(grafo, locacionesRecoger);
        log.info("Ruta óptima calculada: distancia={}, pathLength={}", 
                rutaOptima.distanciaTotal(), rutaOptima.pathCompleto().size());

        var ruta = RutaPick.builder()
                .idRuta(UUID.randomUUID())
                .idOrden(idOrden)
                .pathSeq(rutaOptima.pathCompleto())
                .distanciaEstimada(rutaOptima.distanciaTotal())
                .fecCreacion(OffsetDateTime.now())
                .build();

        var saved = rutaPickPersistence.save(ruta);
        log.info("=== Ruta guardada exitosamente para orden {} (idRuta: {}) ===", idOrden, saved.idRuta());
        return saved;
    }
}