package com.portable.microservices.ms_inventory.movement.domain.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class MovimientoTest {

    @Test
    void isValidForCreationDebeRetornarTrueCuandoDatosSonValidos() {
        Movimiento movimiento = Movimiento.crearEntrada(UUID.randomUUID(), 10L, 5, "Compra", "DOC-1");

        assertTrue(movimiento.isValidForCreation());
    }

    @Test
    void isValidForCreationDebeRetornarFalseCuandoCantidadNoEsPositiva() {
        Movimiento movimiento = Movimiento.crearSalida(UUID.randomUUID(), 10L, 0, "Salida", "DOC-2");

        assertFalse(movimiento.isValidForCreation());
    }

    @Test
    void isValidForCreationDebeRetornarFalseCuandoFaltaUsuario() {
        Movimiento movimiento = Movimiento.crearEntrada(UUID.randomUUID(), null, 1, "Entrada", "DOC-3");

        assertFalse(movimiento.isValidForCreation());
    }
}
