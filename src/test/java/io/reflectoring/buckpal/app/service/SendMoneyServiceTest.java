package io.reflectoring.buckpal.application.domain.service;

import io.reflectoring.buckpal.application.domain.model.Account;
import io.reflectoring.buckpal.application.domain.model.ActivityWindow;
import io.reflectoring.buckpal.application.domain.model.Money;
import io.reflectoring.buckpal.application.port.in.SendMoneyCommand;
import io.reflectoring.buckpal.application.port.out.AccountLock;
import io.reflectoring.buckpal.application.port.out.LoadAccountPort;
import io.reflectoring.buckpal.application.port.out.UpdateAccountStatePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SendMoneyServiceTest {

    @Mock
    private LoadAccountPort loadAccountPort;

    @Mock
    private AccountLock accountLock;

    @Mock
    private UpdateAccountStatePort updateAccountStatePort;

    @Mock
    private MoneyTransferProperties moneyTransferProperties;

    @InjectMocks
    private SendMoneyService sendMoneyService;

    @Test
    void transactionSucceeds() {
        // 1. GIVEN
        SendMoneyCommand command = new SendMoneyCommand(
                new Account.AccountId(1L),
                new Account.AccountId(2L),
                Money.of(300L)
        );

        given(moneyTransferProperties.getMaximumTransferThreshold())
                .willReturn(Money.of(1000L));

        given(loadAccountPort.loadAccount(any(Account.AccountId.class), any(LocalDateTime.class)))
                .willReturn(givenAnAccount(1L))
                .willReturn(givenAnAccount(2L));

        // 2. WHEN
        boolean success = sendMoneyService.sendMoney(command);

        // 3. THEN
        assertThat(success).isTrue();
        verify(updateAccountStatePort, times(2)).updateActivities(any(Account.class));
    }

    private Account givenAnAccount(Long id) {
        return Account.withId(
                new Account.AccountId(id),
                Money.of(1000L),
                new ActivityWindow(new ArrayList<>())
        );
    }
}