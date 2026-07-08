package com.portable.microservices.ms_tracking.picking.domain.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.portable.microservices.ms_tracking.picking.domain.model.GrafoAlmacen;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OptimizadorRutaService {
    private final DijkstraService dijkstraService;

    public record RutaOptima(List<UUID> pathCompleto, BigDecimal distanciaTotal) {
    }

    public RutaOptima optimizar(GrafoAlmacen grafo, List<UUID> locacionesRecoger) {
        if (locacionesRecoger.isEmpty()) {
            return new RutaOptima(List.of(), BigDecimal.ZERO);

        }
        UUID entrada = grafo.nodos().stream()
                .filter(n -> "Entrada".equals(n.tipoNodo()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nodo de entrada no encontrado"))
                .idNodo();
        UUID nodoActual = entrada;
        List<UUID> caminoCompleto = new java.util.LinkedList<>();
        caminoCompleto.add(entrada);

        List<UUID> pendientes = new java.util.ArrayList<>(locacionesRecoger);
        BigDecimal distanciaTotal = BigDecimal.ZERO;
        while (!pendientes.isEmpty()) {
            UUID mejorDestino = null;
            DijkstraService.ResultadoDijkstra mejorRuta = null;
            for (UUID destino : pendientes) {
                var ruta = dijkstraService.caminoMasCorto(grafo, nodoActual, destino);
                if (ruta.camino().isEmpty()) continue;
                if (mejorRuta == null || ruta.distanciaTotal().compareTo(mejorRuta.distanciaTotal()) < 0) {
                    mejorRuta = ruta;
                    mejorDestino = destino;
                }
            }
            if (mejorDestino == null) break;

            caminoCompleto.addAll(mejorRuta.camino().subList(1, mejorRuta.camino().size()));
            distanciaTotal = distanciaTotal.add(mejorRuta.distanciaTotal());
            nodoActual = mejorDestino;
            pendientes.remove(mejorDestino);
        }
        UUID salida = grafo.nodos().stream()
                .filter(n -> "Salida".equals(n.tipoNodo()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nodo de salida no encontrado"))
                .idNodo();
        var rutaSalida = dijkstraService.caminoMasCorto(grafo, nodoActual, salida);
        if (!rutaSalida.camino().isEmpty()) {
            caminoCompleto.addAll(rutaSalida.camino().subList(1, rutaSalida.camino().size()));
            distanciaTotal = distanciaTotal.add(rutaSalida.distanciaTotal());
        }
        return new RutaOptima(caminoCompleto, distanciaTotal);
    }
}
