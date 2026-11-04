package io.reflectoring.buckpal;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import io.reflectoring.buckpal.application.domain.model.Account.AccountId;
import io.reflectoring.buckpal.application.domain.model.Money;
import io.reflectoring.buckpal.application.port.out.LoadAccountPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:io/reflectoring/buckpal/SendMoneySystemTest.sql")
class SendMoneySystemTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private LoadAccountPort loadAccountPort;
    @Test
    void transferenciaCompletaEntreDosCuentas() {
        // Arrange
        AccountId cuenta1 = new AccountId(1L);
        AccountId cuenta2 = new AccountId(2L);
        LocalDateTime baseline = LocalDateTime.now();
        Money antesCuenta1 = loadAccountPort.loadAccount(cuenta1, baseline).calculateBalance();
        Money antesCuenta2 = loadAccountPort.loadAccount(cuenta2, baseline).calculateBalance();
        // Act
        ResponseEntity<Void> response = restTemplate.postForEntity(
                "/accounts/send/1/2/500", null, Void.class);
        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Money despuesCuenta1 = loadAccountPort.loadAccount(cuenta1, baseline).calculateBalance();
        Money despuesCuenta2 = loadAccountPort.loadAccount(cuenta2, baseline).calculateBalance();

        assertThat(despuesCuenta1).isEqualTo(antesCuenta1.minus(Money.of(500L)));
        assertThat(despuesCuenta2).isEqualTo(antesCuenta2.plus(Money.of(500L)));
    }
}