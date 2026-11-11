package io.reflectoring.buckpal.application.domain.service;

import java.time.LocalDateTime;
import jakarta.transaction.Transactional;
import io.reflectoring.buckpal.application.domain.model.Account;
import io.reflectoring.buckpal.application.domain.model.Account.AccountId;
import io.reflectoring.buckpal.application.port.in.DepositMoneyCommand;
import io.reflectoring.buckpal.application.port.in.DepositMoneyUseCase;
import io.reflectoring.buckpal.application.port.out.AccountLock;
import io.reflectoring.buckpal.application.port.out.LoadAccountPort;
import io.reflectoring.buckpal.application.port.out.UpdateAccountStatePort;
import io.reflectoring.buckpal.common.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
@Transactional
public class DepositMoneyService implements DepositMoneyUseCase {

    private final LoadAccountPort loadAccountPort;
    private final AccountLock accountLock;
    private final UpdateAccountStatePort updateAccountStatePort;

    @Override
    public boolean depositMoney(DepositMoneyCommand command) {
        LocalDateTime baselineDate = LocalDateTime.now().minusDays(10);
        Account account = loadAccountPort.loadAccount(command.targetAccountId(), baselineDate);

        AccountId accountId = account.getId()
                .orElseThrow(() -> new IllegalStateException("expected account ID not to be empty"));

        accountLock.lockAccount(accountId);
        if (!account.deposit(command.money(), accountId)) {
            accountLock.releaseAccount(accountId);
            return false;
        }

        updateAccountStatePort.updateActivities(account);
        accountLock.releaseAccount(accountId);
        return true;
    }
}