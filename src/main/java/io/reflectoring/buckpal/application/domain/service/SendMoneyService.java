package io.reflectoring.buckpal.application.domain.service;

import io.reflectoring.buckpal.application.port.in.SendMoneyCommand;
import io.reflectoring.buckpal.application.port.in.SendMoneyUseCase;
import io.reflectoring.buckpal.application.port.out.AccountLock;
import io.reflectoring.buckpal.application.port.out.LoadAccountPort;
import io.reflectoring.buckpal.application.port.out.UpdateAccountStatePort;
import io.reflectoring.buckpal.common.UseCase;
import io.reflectoring.buckpal.application.domain.model.Account;
import io.reflectoring.buckpal.application.domain.model.Account.AccountId;  
import lombok.RequiredArgsConstructor;

import jakarta.transaction.Transactional; 
import java.time.LocalDateTime;

@RequiredArgsConstructor
@UseCase
@Transactional
public class SendMoneyService implements SendMoneyUseCase {

	private final LoadAccountPort loadAccountPort;
	private final AccountLock accountLock;
	private final UpdateAccountStatePort updateAccountStatePort;
	private final MoneyTransferProperties moneyTransferProperties;

	@Override
	public boolean sendMoney(SendMoneyCommand command) {

		checkThreshold(command);

		LocalDateTime baselineDate = LocalDateTime.now().minusDays(10);

		// 1. Cargar cuentas
		Account sourceAccount = loadAccountPort.loadAccount(command.sourceAccountId(), baselineDate);
		Account targetAccount = loadAccountPort.loadAccount(command.targetAccountId(), baselineDate);

		AccountId sourceAccountId = getAccountId(sourceAccount, "source");
		AccountId targetAccountId = getAccountId(targetAccount, "target");

		// 2. Bloqueo y retiro de la cuenta origen
		accountLock.lockAccount(sourceAccountId);
		if (!sourceAccount.withdraw(command.money(), targetAccountId)) {
			accountLock.releaseAccount(sourceAccountId);
			return false;
		}

		// 3. Bloqueo y depósito en la cuenta destino
		accountLock.lockAccount(targetAccountId);
		if (!targetAccount.deposit(command.money(), sourceAccountId)) {
			releaseAccounts(sourceAccountId, targetAccountId);
			return false;
		}

		// 4. Actualización de estado y liberación final
		updateAndRelease(sourceAccount, targetAccount, sourceAccountId, targetAccountId);
		return true;
	}

	// --- Métodos extraídos (Extract Method) ---

	private void checkThreshold(SendMoneyCommand command) {
		if (command.money().isGreaterThan(moneyTransferProperties.getMaximumTransferThreshold())) {
			throw new ThresholdExceededException(moneyTransferProperties.getMaximumTransferThreshold(), command.money());
		}
	}

	private AccountId getAccountId(Account account, String accountType) {
		return account.getId()
				.orElseThrow(() -> new IllegalStateException("expected " + accountType + " account ID not to be empty"));
	}

	private void releaseAccounts(AccountId sourceAccountId, AccountId targetAccountId) {
		accountLock.releaseAccount(sourceAccountId);
		accountLock.releaseAccount(targetAccountId);
	}

	private void updateAndRelease(Account sourceAccount, Account targetAccount, AccountId sourceAccountId, AccountId targetAccountId) {
		updateAccountStatePort.updateActivities(sourceAccount);
		updateAccountStatePort.updateActivities(targetAccount);
		releaseAccounts(sourceAccountId, targetAccountId);
	}
}