package io.reflectoring.buckpal.application.domain;

import java.time.LocalDateTime;
import io.reflectoring.buckpal.application.domain.model.Account.AccountId;
import io.reflectoring.buckpal.application.domain.model.ActivityWindow;
import io.reflectoring.buckpal.application.domain.model.Money;
import org.junit.jupiter.api.Test;

import static io.reflectoring.buckpal.common.ActivityTestData.defaultActivity;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ActivityWindowTest {

    @Test
    void calculatesStartTimestamp() {
        // Arrange (Inicializar)
        ActivityWindow window = new ActivityWindow(
                defaultActivity().withTimestamp(startDate()).build(),
                defaultActivity().withTimestamp(inBetweenDate()).build(),
                defaultActivity().withTimestamp(endDate()).build());

        // Act (Ejecutar)
        LocalDateTime actualStart = window.getStartTimestamp();

        // Assert (Verificar)
        assertEquals(startDate(), actualStart);
    }

    @Test
    void calculatesEndTimestamp() {
        // Arrange
        ActivityWindow window = new ActivityWindow(
                defaultActivity().withTimestamp(startDate()).build(),
                defaultActivity().withTimestamp(inBetweenDate()).build(),
                defaultActivity().withTimestamp(endDate()).build());

        // Act
        LocalDateTime actualEnd = window.getEndTimestamp();

        // Assert
        assertEquals(endDate(), actualEnd);
    }

    @Test
    void calculatesBalance() {
        // Arrange
        AccountId account1 = new AccountId(1L);
        AccountId account2 = new AccountId(2L);

        ActivityWindow window = new ActivityWindow(
                defaultActivity()
                        .withSourceAccount(account1)
                        .withTargetAccount(account2)
                        .withMoney(Money.of(999)).build(),
                defaultActivity()
                        .withSourceAccount(account1)
                        .withTargetAccount(account2)
                        .withMoney(Money.of(1)).build(),
                defaultActivity()
                        .withSourceAccount(account2)
                        .withTargetAccount(account1)
                        .withMoney(Money.of(500)).build());

        // Act
        Money balanceAccount1 = window.calculateBalance(account1);
        Money balanceAccount2 = window.calculateBalance(account2);

        // Assert
        assertEquals(Money.of(-500), balanceAccount1);
        assertEquals(Money.of(500), balanceAccount2);
    }

    private LocalDateTime startDate() {
        return LocalDateTime.of(2019, 8, 3, 0, 0);
    }

    private LocalDateTime inBetweenDate() {
        return LocalDateTime.of(2019, 8, 4, 0, 0);
    }

    private LocalDateTime endDate() {
        return LocalDateTime.of(2019, 8, 5, 0, 0);
    }
}