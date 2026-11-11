package io.reflectoring.buckpal.application.port.in;

import io.reflectoring.buckpal.application.domain.model.Account.AccountId;
import io.reflectoring.buckpal.application.domain.model.Money;

import static java.util.Objects.requireNonNull;

public record DepositMoneyCommand(
        AccountId targetAccountId,
        Money money
) {
    public DepositMoneyCommand(AccountId targetAccountId, Money money) {
        this.targetAccountId = requireNonNull(targetAccountId);
        this.money = requireNonNull(money);
        if (!money.isPositive()) {
            throw new IllegalArgumentException("Money to deposit must be positive");
        }
    }
}