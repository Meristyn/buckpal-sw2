package io.reflectoring.buckpal.application.port.in;

public interface DepositMoneyUseCase {
    boolean depositMoney(DepositMoneyCommand command);
}