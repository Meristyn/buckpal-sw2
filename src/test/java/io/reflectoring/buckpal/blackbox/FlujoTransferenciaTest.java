package io.reflectoring.buckpal.blackbox;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class FlujoTransferenciaTest extends BaseSeleniumTest {

    @Test
    @DisplayName("FL01: Flujo completo - Transferencia exitosa")
    void flujoTransferenciaExitosa() {
        transfer("1", "2", "500");
        
        String msg = message().toLowerCase();
        // El mensaje real dice "transferencia exitosa: 500 de la cuenta 1 a la cuenta 2."
        assertThat(msg).contains("exitosa");
        assertThat(msg).contains("500");
        assertThat(msg).contains("cuenta 1");
        assertThat(msg).contains("cuenta 2");
    }

    @Test
    @DisplayName("FL02: Flujo alterno - Dos transferencias consecutivas")
    void flujoDosTransferencias() {
        // Primera transferencia
        transfer("1", "2", "100");
        assertThat(message().toLowerCase()).contains("exitosa");
        assertThat(message()).contains("100");
        
        // Segunda transferencia
        transfer("1", "2", "200");
        assertThat(message().toLowerCase()).contains("exitosa");
        assertThat(message()).contains("200");
    }

    @Test
    @DisplayName("FL03: Flujo de error - Transferencia fallida")
    void flujoTransferenciaFallida() {
        transfer("1", "2", "99999");
        
        String msg = message().toLowerCase();
        // El mensaje real dice "el monto excede el límite máximo permitido por transferencia."
        assertThat(msg).contains("excede");
        assertThat(msg).contains("límite");
    }
} 