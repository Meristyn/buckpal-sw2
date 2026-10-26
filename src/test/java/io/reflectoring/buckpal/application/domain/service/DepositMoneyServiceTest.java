package io.reflectoring.buckpal.application.domain.service;

import java.time.LocalDateTime;
import io.reflectoring.buckpal.application.domain.model.Account;
import io.reflectoring.buckpal.application.domain.model.Account.AccountId;
import io.reflectoring.buckpal.application.domain.model.Money;
import io.reflectoring.buckpal.application.port.in.DepositMoneyCommand;
import io.reflectoring.buckpal.application.port.out.AccountLock;
import io.reflectoring.buckpal.application.port.out.LoadAccountPort;
import io.reflectoring.buckpal.application.port.out.UpdateAccountStatePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepositMoneyServiceTest {

    @Mock
    private LoadAccountPort loadAccountPort;

    @Mock
    private AccountLock accountLock;

    @Mock
    private UpdateAccountStatePort updateAccountStatePort;

    @Mock
    private Account account;

    @InjectMocks
    private DepositMoneyService depositMoneyService;

    @Test
    void depositSucceeds() {
        // Arrange (Inicializar)
        AccountId targetAccountId = new AccountId(1L);
        Money amount = Money.of(500L);
        DepositMoneyCommand command = new DepositMoneyCommand(targetAccountId, amount);

        when(loadAccountPort.loadAccount(eq(targetAccountId), any(LocalDateTime.class)))
                .thenReturn(account);
        when(account.getId()).thenReturn(java.util.Optional.of(targetAccountId));
        when(account.deposit(eq(amount), any(AccountId.class))).thenReturn(true);

        // Act (Ejecutar)
        boolean success = depositMoneyService.depositMoney(command);

        // Assert (Verificar)
        assertTrue(success);
        verify(accountLock, times(1)).lockAccount(targetAccountId);
        verify(account, times(1)).deposit(eq(amount), any(AccountId.class));
        verify(updateAccountStatePort, times(1)).updateActivities(account);
        verify(accountLock, times(1)).releaseAccount(targetAccountId);
    }
}