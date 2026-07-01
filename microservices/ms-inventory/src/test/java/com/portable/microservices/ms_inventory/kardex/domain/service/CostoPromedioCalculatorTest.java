package com.portable.microservices.ms_inventory.kardex.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.portable.microservices.ms_inventory.kardex.domain.model.Kardex;

class CostoPromedioCalculatorTest {
    private final CostoPromedioCalculator calculator = new CostoPromedioCalculator();

    @Test
    void calcularParaIngresoDebeUsarCostoUnitarioCuandoNoHayStockPrevio() {
        var resultado = calculator.calcularParaIngreso(Optional.empty(), 10, new BigDecimal("12.34567"));

        assertEquals(0, resultado.stockAnterior());
        assertEquals(10, resultado.stockActual());
        assertEquals(new BigDecimal("12.3457"), resultado.costoPromNuevo());
    }

    @Test
    void calcularParaIngresoDebePromediarCostoCuandoHayStockPrevio() {
        Kardex ultimo = kardex(10, new BigDecimal("10.0000"));

        var resultado = calculator.calcularParaIngreso(Optional.of(ultimo), 10, new BigDecimal("20.0000"));

        assertEquals(10, resultado.stockAnterior());
        assertEquals(20, resultado.stockActual());
        assertEquals(new BigDecimal("15.0000"), resultado.costoPromNuevo());
    }

    @Test
    void calcularParaSalidaDebeMantenerCostoPromedioYNoRetornarStockNegativo() {
        Kardex ultimo = kardex(5, new BigDecimal("8.2500"));

        var resultado = calculator.calcularParaSalida(Optional.of(ultimo), 10);

        assertEquals(5, resultado.stockAnterior());
        assertEquals(0, resultado.stockActual());
        assertEquals(new BigDecimal("8.2500"), resultado.costoPromNuevo());
    }

    private Kardex kardex(Integer stockActual, BigDecimal costoProm) {
        return new Kardex(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 0, 0, 0,
                stockActual, costoProm);
    }
}
