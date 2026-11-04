package io.reflectoring.buckpal.blackbox;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

/**
 * Pruebas funcionales (caja negra) de análisis de valores límite sobre la
 * transferencia de dinero, ejecutadas con Selenium sobre la interfaz web.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:io/reflectoring/buckpal/blackbox/ValoresLimiteTest.sql")
@Sql(scripts = "classpath:io/reflectoring/buckpal/blackbox/limpiar-datos.sql",
		executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ValoresLimiteTest {

	private static final String CUENTA_SALDO = "1";      // saldo inicial 5000
	private static final String CUENTA_DESTINO = "2";    // saldo inicial 1000
	private static final String CUENTA_LIMITE = "3";     // saldo inicial 20000

	private static final String MSG_LIMITE =
			"El monto excede el límite máximo permitido por transferencia.";

	@LocalServerPort
	private int port;

	private WebDriver driver;
	private WebDriverWait wait;

	@BeforeEach
	void abrirNavegador() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless=new");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-dev-shm-usage");
		options.addArguments("--remote-allow-origins=*");

		driver = new ChromeDriver(options);
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		driver.get("http://localhost:" + port + "/");
	}

	@AfterEach
	void cerrarNavegador() {
		if (driver != null) {
			driver.quit();
		}
	}

	// ---------------------------------------------------------------------
	// Límites respecto al saldo de la cuenta de origen (saldo = 5000)
	// ---------------------------------------------------------------------

	@Test
	@DisplayName("VL01: Monto igual al saldo (5000) -> transferencia exitosa, origen queda en 0")
	void vl01_montoIgualAlSaldo() {
		transferir(CUENTA_SALDO, CUENTA_DESTINO, "5000");

		assertThat(tipoMensaje()).isEqualTo("success");
		assertThat(mensaje()).isEqualTo("Transferencia exitosa: 5000 de la cuenta 1 a la cuenta 2.");
		assertThat(consultarSaldo(CUENTA_SALDO)).isEqualTo("0");
		assertThat(consultarSaldo(CUENTA_DESTINO)).isEqualTo("6000");
	}

	@Test
	@DisplayName("VL02: Monto igual al saldo + 1 (5001) -> saldo insuficiente, saldos sin cambios")
	void vl02_montoSaldoMasUno() {
		transferir(CUENTA_SALDO, CUENTA_DESTINO, "5001");

		assertThat(tipoMensaje()).isEqualTo("error");
		assertThat(mensaje()).isEqualTo("Saldo insuficiente en la cuenta 1.");
		assertThat(consultarSaldo(CUENTA_SALDO)).isEqualTo("5000");
		assertThat(consultarSaldo(CUENTA_DESTINO)).isEqualTo("1000");
	}

	// ---------------------------------------------------------------------
	// Límite inferior del monto
	// ---------------------------------------------------------------------

	@Test
	@DisplayName("VL03: Monto mínimo (1) -> transferencia exitosa")
	void vl03_montoMinimo() {
		transferir(CUENTA_SALDO, CUENTA_DESTINO, "1");

		assertThat(tipoMensaje()).isEqualTo("success");
		assertThat(mensaje()).isEqualTo("Transferencia exitosa: 1 de la cuenta 1 a la cuenta 2.");
		assertThat(consultarSaldo(CUENTA_SALDO)).isEqualTo("4999");
		assertThat(consultarSaldo(CUENTA_DESTINO)).isEqualTo("1001");
	}

	// ---------------------------------------------------------------------
	// Límites respecto al máximo por transferencia (10000)
	// ---------------------------------------------------------------------

	@Test
	@DisplayName("VL04: Monto justo debajo del límite (9999) -> transferencia exitosa")
	void vl04_montoDebajoDelLimite() {
		transferir(CUENTA_LIMITE, CUENTA_DESTINO, "9999");

		assertThat(tipoMensaje()).isEqualTo("success");
		assertThat(mensaje()).isEqualTo("Transferencia exitosa: 9999 de la cuenta 3 a la cuenta 2.");
		assertThat(consultarSaldo(CUENTA_LIMITE)).isEqualTo("10001");
		assertThat(consultarSaldo(CUENTA_DESTINO)).isEqualTo("10999");
	}

	@Test
	@DisplayName("VL05: Monto igual al límite (10000) -> transferencia exitosa")
	void vl05_montoIgualAlLimite() {
		transferir(CUENTA_LIMITE, CUENTA_DESTINO, "10000");

		assertThat(tipoMensaje()).isEqualTo("success");
		assertThat(mensaje()).isEqualTo("Transferencia exitosa: 10000 de la cuenta 3 a la cuenta 2.");
		assertThat(consultarSaldo(CUENTA_LIMITE)).isEqualTo("10000");
		assertThat(consultarSaldo(CUENTA_DESTINO)).isEqualTo("11000");
	}

	@Test
	@DisplayName("VL06: Monto justo encima del límite (10001) -> rechazada por límite, saldos sin cambios")
	void vl06_montoEncimaDelLimite() {
		transferir(CUENTA_LIMITE, CUENTA_DESTINO, "10001");

		assertThat(tipoMensaje()).isEqualTo("error");
		assertThat(mensaje()).isEqualTo(MSG_LIMITE);
		assertThat(consultarSaldo(CUENTA_LIMITE)).isEqualTo("20000");
		assertThat(consultarSaldo(CUENTA_DESTINO)).isEqualTo("1000");
	}

	// ---------------------------------------------------------------------
	// Acciones sobre la interfaz
	// ---------------------------------------------------------------------

	private void transferir(String origen, String destino, String monto) {
		escribir(By.id("source-account-id"), origen);
		escribir(By.id("target-account-id"), destino);
		escribir(By.id("amount"), monto);
		enviar(By.id("transfer-submit"));
	}

	private String consultarSaldo(String cuenta) {
		escribir(By.id("balance-account-id"), cuenta);
		enviar(By.id("balance-submit"));
		return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("balance-result")))
				.getText().trim();
	}

	private String mensaje() {
		return driver.findElement(By.id("message")).getText().trim();
	}

	private String tipoMensaje() {
		String clases = driver.findElement(By.id("message")).getAttribute("class");
		return clases.contains("success") ? "success" : clases.contains("error") ? "error" : clases;
	}

	private void escribir(By campo, String valor) {
		WebElement input = driver.findElement(campo);
		input.clear();
		input.sendKeys(valor);
	}

	/** Envía el formulario y espera a que se cargue la página de respuesta. */
	private void enviar(By boton) {
		WebElement html = driver.findElement(By.tagName("html"));
		driver.findElement(boton).click();
		wait.until(ExpectedConditions.stalenessOf(html));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("message")));
	}

}
