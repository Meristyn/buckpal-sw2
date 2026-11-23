package io.reflectoring.buckpal.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

interface ActivityRepository extends JpaRepository<ActivityJpaEntity, Long> {

	@Query("select a from ActivityJpaEntity a " +
			"where a.ownerAccountId = :ownerAccountId " +
			"and a.timestamp >= :since")
	List<ActivityJpaEntity> findByOwnerSince(
			@Param("ownerAccountId") long ownerAccountId,
			@Param("since") LocalDateTime since);

	//Consulta unificada(Extract Method)
	@Query("select sum(a.amount) from ActivityJpaEntity a " +
			"where a.ownerAccountId = :accountId " +
			"and a.timestamp < :until " +
			"and ((:deposit = true and a.targetAccountId = :accountId) " +
			"  or (:deposit = false and a.sourceAccountId = :accountId))")
	Optional <Long> getBalanceUntil(
			@Param("accountId") long accountId,
			@Param("until") LocalDateTime until,
			@Param("deposit") boolean deposit);

	default Optional <Long> getDepositBalanceUntil(long accountId, LocalDateTime until){
		return getBalanceUntil(accountId, until, true);

	}

	default Optional <Long> getWithdrawalBalanceUntil(long accountId, LocalDateTime until){
		return getBalanceUntil(accountId, until, false);
	}

}
