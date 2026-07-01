package com.portable.microservices.ms_tracking.picking.domain.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.portable.microservices.ms_tracking.picking.domain.model.AristaGrafo;
import com.portable.microservices.ms_tracking.picking.domain.model.GrafoAlmacen;

@Component
public class DijkstraService {
    public record ResultadoDijkstra(List<UUID> camino, BigDecimal distanciaTotal) {
    }

    public ResultadoDijkstra caminoMasCorto(GrafoAlmacen grafo, UUID nodoInicio, UUID nodoFin) {
        var adj = grafo.aristas().stream()
                .collect(Collectors.groupingBy(AristaGrafo::idNodoOrigen,
                        Collectors.toMap(
                                AristaGrafo::idNodoDestino,
                                AristaGrafo::peso)));
        Map<UUID, BigDecimal> distancias = new HashMap<>();
        Map<UUID, UUID> predecesores = new HashMap<>();
        Set<UUID> visitados = new HashSet<>();
        for (var nodo : grafo.nodos()) {
            distancias.put(nodo.idNodo(), BigDecimal.valueOf(Double.MAX_VALUE));
        }
        distancias.put(nodoInicio, BigDecimal.ZERO);
        PriorityQueue<UUID> pq = new PriorityQueue<>(
                Comparator.comparing((UUID n) -> distancias.getOrDefault(n, BigDecimal.valueOf(Double.MAX_VALUE))));
        pq.add(nodoInicio);
        while (!pq.isEmpty()) {
            UUID nodoActual = pq.poll();
            if (visitados.contains(nodoActual))
                continue;
            visitados.add(nodoActual);
            if (nodoActual.equals(nodoFin))
                break;
            var vecinos = adj.getOrDefault(nodoActual, Collections.emptyMap());
            for (var entry : vecinos.entrySet()) {
                UUID vecino = entry.getKey();
                BigDecimal peso = entry.getValue();
                if (visitados.contains(vecino))
                    continue;
                BigDecimal nuevaDistancia = distancias.get(nodoActual).add(peso);
                if (nuevaDistancia.compareTo(distancias.get(vecino)) < 0) {
                    distancias.put(vecino, nuevaDistancia);
                    predecesores.put(vecino, nodoActual);
                    pq.add(vecino);
                }
            }
        }
        List<UUID> camino = new LinkedList<>();
        for (UUID at = nodoFin; at != null; at = predecesores.get(at)) {
            camino.addFirst(at);
        }
        if (camino.isEmpty() || !camino.get(0).equals(nodoInicio)) {
            return new ResultadoDijkstra(Collections.emptyList(), BigDecimal.ZERO);
        }
        return new ResultadoDijkstra(camino, distancias.get(nodoFin));
    }
}
