package io.reflectoring.buckpal.blackbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EquivalenciaMontoTest extends BaseSeleniumTest {

    @Test
    @DisplayName("CP06: Monto igual a cero (CE Inválida) muestra error y saldo de cuenta 1 sigue en 5000")
    void cp06_montoCero() {
        transfer("1", "2", "0");

        String msg = message();
        assertFalse(msg.isBlank(), "Debe mostrarse un mensaje de error cuando el monto es 0");
        assertTrue(balance("1").contains("5000"), "El saldo de la cuenta 1 no debió cambiar (5000)");
    }

    @Test
    @DisplayName("CP07: Monto negativo (CE Inválida) muestra error y saldo de cuenta 1 sigue en 5000")
    void cp07_montoNegativo() {
        transfer("1", "2", "-100");

        String msg = message();
        assertFalse(msg.isBlank(), "Debe mostrarse un mensaje de error cuando el monto es negativo");
        assertTrue(balance("1").contains("5000"), "El saldo de la cuenta 1 no debió cambiar (5000)");
    }

    @Test
    @DisplayName("CP08: Monto con texto (CE Inválida) muestra error y saldo de cuenta 1 sigue en 5000")
    void cp08_montoConTexto() {
        transfer("1", "2", "abc");

        String msg = message();
        assertFalse(msg.isBlank(), "Debe mostrarse un mensaje de error cuando el monto contiene texto");
        assertTrue(balance("1").contains("5000"), "El saldo de la cuenta 1 no debió cambiar (5000)");
    }

    @Test
    @DisplayName("CP09: Monto vacío (CE Inválida) muestra error y saldo de cuenta 1 sigue en 5000")
    void cp09_montoVacio() {
        transfer("1", "2", "");

        String msg = message();
        assertFalse(msg.isBlank(), "Debe mostrarse un mensaje de error cuando el monto está vacío");
        assertTrue(balance("1").contains("5000"), "El saldo de la cuenta 1 no debió cambiar (5000)");
    }

    @Test
    @DisplayName("CP10: Monto decimal (CE Inválida) muestra error y saldo de cuenta 1 sigue en 5000")
    void cp10_montoDecimal() {
        transfer("1", "2", "100.50");

        String msg = message();
        assertFalse(msg.isBlank(), "Debe mostrarse un mensaje de error cuando el monto es decimal");
        assertTrue(balance("1").contains("5000"), "El saldo de la cuenta 1 no debió cambiar (5000)");
    }
}