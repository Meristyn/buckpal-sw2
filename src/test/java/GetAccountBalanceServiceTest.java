package io.reflectoring.buckpal.application.domain.service;

import io.reflectoring.buckpal.application.domain.model.Account;
import io.reflectoring.buckpal.application.domain.model.Account.AccountId;
import io.reflectoring.buckpal.application.domain.model.Activity;
import io.reflectoring.buckpal.application.domain.model.ActivityWindow;
import io.reflectoring.buckpal.application.domain.model.Money;
import io.reflectoring.buckpal.application.port.in.GetAccountBalanceUseCase.GetAccountBalanceQuery;
import io.reflectoring.buckpal.application.port.out.LoadAccountPort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GetAccountBalanceServiceTest {

    private LoadAccountPort loadAccountPort;
    private GetAccountBalanceService service;

    @BeforeEach
    void setUp() {
        loadAccountPort = mock(LoadAccountPort.class);
        service = new GetAccountBalanceService(loadAccountPort);
    }

    @Test
    void shouldReturnAccountBalance() {
        // ARRANGE: preparar los datos de prueba
        AccountId accountId = new AccountId(1L);
        AccountId anotherAccountId = new AccountId(2L);

        LocalDateTime now = LocalDateTime.now();

        Activity salary = new Activity(
                accountId,
                anotherAccountId,
                accountId,
                now,
                Money.of(1500)
        );

        Activity withdrawal = new Activity(
                accountId,
                accountId,
                anotherAccountId,
                now.plusMinutes(1),
                Money.of(200)
        );

        ActivityWindow activityWindow =
                new ActivityWindow(salary, withdrawal);

        Account account = Account.withId(
                accountId,
                Money.of(1000),
                activityWindow
        );

        GetAccountBalanceQuery query =
                new GetAccountBalanceQuery(accountId);

        Money expectedBalance = Money.of(2300);

        when(loadAccountPort.loadAccount(
                eq(accountId), any(LocalDateTime.class)))
                .thenReturn(account);

        // ACT: ejecutar el servicio
        Money result = service.getAccountBalance(query);

        // ASSERT: verificar el resultado y la interacción
        assertEquals(expectedBalance, result);

        verify(loadAccountPort).loadAccount(
                eq(accountId), any(LocalDateTime.class));
    }
}
