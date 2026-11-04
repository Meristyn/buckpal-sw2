package io.reflectoring.buckpal.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.reflectoring.buckpal.application.domain.model.Account.AccountId;
import io.reflectoring.buckpal.application.domain.model.Money;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

class SendMoneyCommandTest {

    @Test
    void comandoValidoSeCrea() {
        // Arrange
        AccountId source = new AccountId(41L);
        AccountId target = new AccountId(42L);
        Money money = Money.of(500L);

        // Act
        SendMoneyCommand command = new SendMoneyCommand(source, target, money);

        // Assert
        assertThat(command.sourceAccountId()).isEqualTo(source);
        assertThat(command.targetAccountId()).isEqualTo(target);
        assertThat(command.money()).isEqualTo(money);
    }

    @Test
    void origenNuloLanzaExcepcion() {
        AccountId target = new AccountId(42L);
        Money money = Money.of(500L);

        assertThrows(ConstraintViolationException.class,
                () -> new SendMoneyCommand(null, target, money));
    }

    @Test
    void destinoNuloLanzaExcepcion() {
        AccountId source = new AccountId(41L);
        Money money = Money.of(500L);

        assertThrows(ConstraintViolationException.class,
                () -> new SendMoneyCommand(source, null, money));
    }

    @Test
    void montoCeroLanzaExcepcion() {
        AccountId source = new AccountId(41L);
        AccountId target = new AccountId(42L);

        assertThrows(ConstraintViolationException.class,
                () -> new SendMoneyCommand(source, target, Money.of(0L)));
    }
}