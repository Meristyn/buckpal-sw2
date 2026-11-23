package io.reflectoring.buckpal.adapter.in.web;

import io.reflectoring.buckpal.application.domain.model.Account.AccountId;
import io.reflectoring.buckpal.application.domain.model.Money;
import io.reflectoring.buckpal.application.domain.service.ThresholdExceededException;
import io.reflectoring.buckpal.application.port.in.GetAccountBalanceUseCase;
import io.reflectoring.buckpal.application.port.in.GetAccountBalanceUseCase.GetAccountBalanceQuery;
import io.reflectoring.buckpal.application.port.in.SendMoneyCommand;
import io.reflectoring.buckpal.application.port.in.SendMoneyUseCase;
import io.reflectoring.buckpal.common.WebAdapter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@WebAdapter
@Controller
@RequiredArgsConstructor
class AccountWebController {

	static final String VIEW = "index";

	private final SendMoneyUseCase sendMoneyUseCase;
	private final GetAccountBalanceUseCase getAccountBalanceUseCase;

	@GetMapping("/")
	String index() {
		return VIEW;
	}

	@PostMapping("/balance")
	String balance(
			@RequestParam(name = "accountId", required = false) String accountId,
			Model model) {

		model.addAttribute("balanceAccountId", accountId);
		try {
			Long id = parseLong(accountId, "ID de la cuenta");
			Money balance = getAccountBalanceUseCase.getAccountBalance(
					new GetAccountBalanceQuery(new AccountId(id)));
			model.addAttribute("balanceResult", balance.getAmount().toString());
			success(model, "Saldo de la cuenta " + id + ": " + balance.getAmount());
		} catch (InvalidInputException e) {
			error(model, e.getMessage());
		} catch (EntityNotFoundException e) {
			String safeId = accountId != null ? accountId.trim() : "";
			error(model, "La cuenta " + safeId + " no existe.");
		}
		return VIEW;
	}

	@PostMapping("/transfer")
	String transfer(
			@RequestParam(name = "sourceAccountId", required = false) String sourceAccountId,
			@RequestParam(name = "targetAccountId", required = false) String targetAccountId,
			@RequestParam(name = "amount", required = false) String amount,
			Model model) {

		model.addAttribute("sourceAccountId", sourceAccountId);
		model.addAttribute("targetAccountId", targetAccountId);
		model.addAttribute("amount", amount);
		try {
			Long source = parseLong(sourceAccountId, "Cuenta de origen");
			Long target = parseLong(targetAccountId, "Cuenta de destino");
			Long value = parsePositiveLong(amount, "Monto");

			SendMoneyCommand command = new SendMoneyCommand(
					new AccountId(source),
					new AccountId(target),
					Money.of(value));

			if (sendMoneyUseCase.sendMoney(command)) {
				success(model, "Transferencia exitosa: " + value
						+ " de la cuenta " + source + " a la cuenta " + target + ".");
			} else {
				error(model, "Saldo insuficiente en la cuenta " + source + ".");
			}
			showBalance(model, source);
		} catch (InvalidInputException e) {
			handleTransferError(model, e.getMessage(), sourceAccountId, true);
		} catch (ConstraintViolationException e) {
			handleTransferError(model, "El monto debe ser mayor que cero.", sourceAccountId, true);
		} catch (ThresholdExceededException e) {
			handleTransferError(model, "El monto excede el límite máximo permitido por transferencia.", sourceAccountId, true);
		} catch (EntityNotFoundException e) {
			handleTransferError(model, "Una de las cuentas no existe.", sourceAccountId, false);
		}
		return VIEW;
	}

	private void handleTransferError(Model model, String message, String sourceAccountId, boolean refreshBalance) {
		error(model, message);
		if (refreshBalance) {
			showBalanceIfSourceIsKnown(model, sourceAccountId);
		}
	}

	private void showBalance(Model model, Long sourceAccountId) {
		Money balance = getAccountBalanceUseCase.getAccountBalance(
				new GetAccountBalanceQuery(new AccountId(sourceAccountId)));
		model.addAttribute("balanceResult", balance.getAmount().toString());
	}

	private void showBalanceIfSourceIsKnown(Model model, String sourceAccountId) {
		if (sourceAccountId == null || sourceAccountId.isBlank()) {
			return;
		}
		try {
			showBalance(model, Long.valueOf(sourceAccountId.trim()));
		} catch (NumberFormatException ignored) {
			// No hay saldo que mostrar si la cuenta de origen no es numérica.
		}
	}

	private static Long parsePositiveLong(String raw, String fieldName) {
		Long value = parseLong(raw, fieldName);
		if (value <= 0) {
			throw new InvalidInputException("El monto debe ser mayor que cero.");
		}
		return value;
	}

	private static Long parseLong(String raw, String fieldName) {
		if (raw == null || raw.isBlank()) {
			throw new InvalidInputException("El campo '" + fieldName + "' es obligatorio.");
		}
		try {
			return Long.valueOf(raw.trim());
		} catch (NumberFormatException e) {
			throw new InvalidInputException("El campo '" + fieldName + "' debe ser un número entero.");
		}
	}

	private static void success(Model model, String message) {
		model.addAttribute("messageType", "success");
		model.addAttribute("message", message);
	}

	private static void error(Model model, String message) {
		model.addAttribute("messageType", "error");
		model.addAttribute("message", message);
	}

	private static class InvalidInputException extends RuntimeException {
		InvalidInputException(String message) {
			super(message);
		}
	}

}