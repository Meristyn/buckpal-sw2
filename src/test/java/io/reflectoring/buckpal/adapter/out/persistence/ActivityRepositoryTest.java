package io.reflectoring.buckpal.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
@DataJpaTest
class ActivityRepositoryTest {
    @Autowired
    private ActivityRepository activityRepository;
    @Test
    void getDepositBalanceUntilSumaDepositosDeLaCuenta() {
        // Arrange: depósitos donde owner == target == 1
        activityRepository.save(activity(1L, 1L, 1L, 500L, LocalDateTime.of(2018, 8, 8, 8, 0))); // owner=1, source=1, target=1
        activityRepository.save(activity(1L, 1L, 1L, 300L, LocalDateTime.of(2018, 8, 8, 9, 0))); // owner=1, source=1, target=1
        activityRepository.save(activity(2L, 2L, 2L, 200L, LocalDateTime.of(2018, 8, 8, 10, 0))); // owner=2, target=2 → no suma a 1
        // Act
        Optional<Long> balance = activityRepository.getDepositBalanceUntil(1L, LocalDateTime.of(2020, 1, 1, 0, 0));
        // Assert
        assertThat(balance).contains(800L);
    }
    @Test
    void getWithdrawalBalanceUntilSumaRetirosDeLaCuenta() {
        // Arrange: 1 retiro de la cuenta 1 por 200
        activityRepository.save(activity(1L, 1L, 2L, 200L, LocalDateTime.of(2018, 8, 8, 8, 0)));
        // Act
        Optional<Long> balance = activityRepository.getWithdrawalBalanceUntil(1L, LocalDateTime.of(2020, 1, 1, 0, 0));
        // Assert
        assertThat(balance).contains(200L);
    }
    private ActivityJpaEntity activity(long owner, long source, long target, long amount, LocalDateTime timestamp) {
        ActivityJpaEntity entity = new ActivityJpaEntity();
        entity.setOwnerAccountId(owner);
        entity.setSourceAccountId(source);
        entity.setTargetAccountId(target);
        entity.setAmount(amount);
        entity.setTimestamp(timestamp);
        return entity;
    }
}
