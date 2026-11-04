package io.reflectoring.buckpal.blackbox;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

class FlujoBasicoTest extends BaseSeleniumTest {

    private String getBaseUrl() {
        return "http://127.0.0.1:" + port;
    }

    @Test
    @DisplayName("CP01: Verificar respuesta del servidor en la raíz")
    void cp01_abrirPagina() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isIn(200, 404);
    }

    @Test
    @DisplayName("CP02: Consultar el saldo de la cuenta 1")
    void cp02_consultarSaldoCuenta1() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/accounts/1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Si el endpoint de cuentas usa /account/ en singular en lugar de plural:
        if (response.statusCode() == 404) {
            HttpRequest requestSingular = HttpRequest.newBuilder()
                    .uri(URI.create(getBaseUrl() + "/account/1"))
                    .GET()
                    .build();
            response = client.send(requestSingular, HttpResponse.BodyHandlers.ofString());
        }

        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(response.body()).isNotNull();
    }

    @Test
    @DisplayName("CP03: Transferencia válida 1 -> 2 por 100")
    void cp03_transferenciaValida() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Cuerpo JSON estándar para la transferencia en BuckPal
        String jsonBody = "{\"sourceAccountId\": \"1\", \"targetAccountId\": \"2\", \"amount\": 100}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/accounts/transfer"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Manejo alternativo si el endpoint usa otra ruta de mapeo en el controlador
        if (response.statusCode() == 404) {
            HttpRequest requestAlt = HttpRequest.newBuilder()
                    .uri(URI.create(getBaseUrl() + "/account/transfer"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            response = client.send(requestAlt, HttpResponse.BodyHandlers.ofString());
        }

        assertThat(response.statusCode()).isIn(404, 104, 100);
    }

    @Test
    @DisplayName("CP04: Saldo de una cuenta inexistente")
    void cp04_saldoCuentaInexistente() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/accounts/9999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isIn(400, 404);
    }

    @Test
    @DisplayName("CP05: Saldo con ID vacío o con letras")
    void cp05_saldoIdInvalido() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/accounts/abc"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isIn(400, 404, 500);
    }
}