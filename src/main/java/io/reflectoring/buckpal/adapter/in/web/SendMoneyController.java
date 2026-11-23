package io.reflectoring.buckpal.adapter.in.web;

import io.reflectoring.buckpal.application.domain.model.Account.AccountId;
import io.reflectoring.buckpal.application.domain.model.Money;
import io.reflectoring.buckpal.application.port.in.SendMoneyCommand;
import io.reflectoring.buckpal.application.port.in.SendMoneyUseCase;
import io.reflectoring.buckpal.common.WebAdapter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@WebAdapter
@RestController
@RequiredArgsConstructor
class SendMoneyController {

	private final SendMoneyUseCase sendMoneyUseCase;

	@PostMapping(path = "/accounts/send")
	ResponseEntity<Boolean> sendMoney(@Valid @RequestBody SendMoneyRequest request) {
		SendMoneyCommand command = new SendMoneyCommand(
				new AccountId(request.sourceAccountId()),
				new AccountId(request.targetAccountId()),
				Money.of(request.amount()));

		boolean executed = sendMoneyUseCase.sendMoney(command);
		if (executed) {
			return ResponseEntity.ok(true);
		}
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(false);
	}

	record SendMoneyRequest(
			@NotNull Long sourceAccountId,
			@NotNull Long targetAccountId,
			@NotNull @Positive Long amount) {
	}

}