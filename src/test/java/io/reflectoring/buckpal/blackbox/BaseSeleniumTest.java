package io.reflectoring.buckpal.blackbox;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("demo")
public class BaseSeleniumTest {
    protected WebDriver driver;
    protected String baseUrl = "http://localhost:8080";
    @LocalServerPort
    protected int port;


    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--headless=new");

        driver = new ChromeDriver(options);
        driver.get(baseUrl);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    public void transfer(String source, String target, String amount) {
        var sourceInput = driver.findElement(org.openqa.selenium.By.name("sourceAccountId"));
        var targetInput = driver.findElement(org.openqa.selenium.By.name("targetAccountId"));
        var amountInput = driver.findElement(org.openqa.selenium.By.name("amount"));
        var form = driver.findElement(org.openqa.selenium.By.id("transfer-form"));

        sourceInput.clear();
        sourceInput.sendKeys(source);

        targetInput.clear();
        targetInput.sendKeys(target);

        amountInput.clear();
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].value = arguments[1];", amountInput, amount);

        form.submit();
    }

    public String balance(String accountId) {
        try {
            // Busca el elemento del saldo específico de la cuenta (ej. id="balance-1")
            return driver.findElement(org.openqa.selenium.By.id("balance-" + accountId)).getText();
        } catch (Exception e) {
            // Si no encuentra un ID específico, devuelve todo el texto visible de la página para buscar el saldo
            return driver.findElement(org.openqa.selenium.By.tagName("body")).getText();
        }
    }

    public String message() {
        try {
            // Intenta capturar el texto de un elemento con id="message" o alguna alerta de error
            return driver.findElement(org.openqa.selenium.By.id("message")).getText();
        } catch (Exception e) {
            try {
                return driver.findElement(org.openqa.selenium.By.className("alert")).getText();
            } catch (Exception ex) {
                return "";
            }
        }
    }
}