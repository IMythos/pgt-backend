package com.portable.microservices.ms_tracking.picking.domain.service;

import com.portable.microservices.ms_tracking.picking.domain.model.AristaGrafo;
import com.portable.microservices.ms_tracking.picking.domain.model.GrafoAlmacen;
import com.portable.microservices.ms_tracking.picking.domain.model.NodoGrafo;
import com.portable.microservices.ms_tracking.picking.infrastructure.client.dto.LocationResponse;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ConstructorGrafoService {

    // Identificadores fijos para que el Frontend y el Optimizador reconozcan las puertas
    public static final UUID ENTRADA_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final UUID SALIDA_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    public GrafoAlmacen construirGrafo(List<LocationResponse> locaciones, NodoGrafo nodoEntradaOmitido) {
        List<NodoGrafo> todosLosNodos = new ArrayList<>();
        
        // 1. Mapear los estantes reales
        for (LocationResponse loc : locaciones) {
            todosLosNodos.add(NodoGrafo.builder()
                    .idNodo(loc.getIdLocacion())
                    .zona(loc.getZona())
                    .pasillo(loc.getPasillo())
                    .estante(loc.getEstante())
                    .tipoNodo("RACK") // Importante para diferenciar
                    .posX(loc.getPosX()) 
                    .posY(loc.getPosY()) 
                    .build());
        }

        // 2. CREAR LAS PUERTAS FÍSICAS (Centro inferior del almacén: X=40, Y=0)
        todosLosNodos.add(NodoGrafo.builder()
                .idNodo(ENTRADA_ID).zona("Base").pasillo("Base").estante("Entrada")
                .tipoNodo("Entrada").posX(40).posY(0).build());
        todosLosNodos.add(NodoGrafo.builder()
                .idNodo(SALIDA_ID).zona("Base").pasillo("Base").estante("Salida")
                .tipoNodo("Salida").posX(40).posY(0).build());

        List<AristaGrafo> todasLasAristas = new ArrayList<>();

        // 3. CONEXIONES VERTICALES (Pasillos)
        Map<Integer, List<NodoGrafo>> porPasilloX = todosLosNodos.stream()
                .filter(n -> "RACK".equals(n.tipoNodo()))
                .collect(Collectors.groupingBy(NodoGrafo::posX));

        for (List<NodoGrafo> nodosPasillo : porPasilloX.values()) {
            nodosPasillo.sort(Comparator.comparingInt(NodoGrafo::posY));
            
            for (int i = 0; i < nodosPasillo.size() - 1; i++) {
                NodoGrafo actual = nodosPasillo.get(i);
                NodoGrafo siguiente = nodosPasillo.get(i + 1);
                
                long distancia = Math.abs(actual.posY() - siguiente.posY());
                BigDecimal peso = BigDecimal.valueOf(distancia);
                
                todasLasAristas.add(new AristaGrafo(actual.idNodo(), siguiente.idNodo(), peso));
                todasLasAristas.add(new AristaGrafo(siguiente.idNodo(), actual.idNodo(), peso));
            }
        }

        Set<Integer> crucesPermitidosY = Set.of(10, 90);
        Map<Integer, List<NodoGrafo>> porCruceY = todosLosNodos.stream()
                .filter(nodo -> "RACK".equals(nodo.tipoNodo()) && crucesPermitidosY.contains(nodo.posY()))
                .collect(Collectors.groupingBy(NodoGrafo::posY));

        for (List<NodoGrafo> nodosCruce : porCruceY.values()) {
            nodosCruce.sort(Comparator.comparingInt(NodoGrafo::posX));
            
            for (int i = 0; i < nodosCruce.size() - 1; i++) {
                NodoGrafo actual = nodosCruce.get(i);
                NodoGrafo siguiente = nodosCruce.get(i + 1);
                
                long distancia = Math.abs(actual.posX() - siguiente.posX());
                BigDecimal peso = BigDecimal.valueOf(distancia);
                
                todasLasAristas.add(new AristaGrafo(actual.idNodo(), siguiente.idNodo(), peso));
                todasLasAristas.add(new AristaGrafo(siguiente.idNodo(), actual.idNodo(), peso));
            }
        }

        List<NodoGrafo> cruceInferior = porCruceY.getOrDefault(10, new ArrayList<>());
        for (NodoGrafo nodo : cruceInferior) {
            if (nodo.posX() == 30 || nodo.posX() == 50) {
                long distancia = Math.abs(nodo.posX() - 40) + Math.abs(nodo.posY() - 0);
                BigDecimal peso = BigDecimal.valueOf(distancia);
                
                todasLasAristas.add(new AristaGrafo(ENTRADA_ID, nodo.idNodo(), peso));
                todasLasAristas.add(new AristaGrafo(nodo.idNodo(), ENTRADA_ID, peso));
                
                todasLasAristas.add(new AristaGrafo(SALIDA_ID, nodo.idNodo(), peso));
                todasLasAristas.add(new AristaGrafo(nodo.idNodo(), SALIDA_ID, peso));
            }
        }

        return GrafoAlmacen.builder()
                .nodos(todosLosNodos)
                .aristas(todasLasAristas)
                .build();
    }
}