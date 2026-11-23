package io.reflectoring.buckpal.adapter.out.persistence;

import io.reflectoring.buckpal.application.port.out.AccountLock;
import io.reflectoring.buckpal.application.domain.model.Account.AccountId;
import org.springframework.stereotype.Component;

/**
 * Implementación Null Object de {@link AccountLock}.
 * <p>
 * No se necesita bloqueo de cuentas en esta version. Así los servicios
 * pueden llamar a lockAccount/releaseAccount sin comprobar si existe un
 * mecanismo de bloqueo real. Para agregar bloqueo real (por ejemplo, bloqueo
 * pesimista en base de datos), crear otro adaptador y este se reemplaza.
 */


@Component
class NoOpAccountLock implements AccountLock {

	@Override
	public void lockAccount(AccountId accountId) {
		// Intencionalmente vacío (Null Object). esta implementación no bloquea cuentas.
	}

	@Override
	public void releaseAccount(AccountId accountId) {
		// Intencionalmente vacío (Null Object).No hay bloqueo que liberar.
	}

}
