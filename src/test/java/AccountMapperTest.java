
package io.reflectoring.buckpal.adapter.out.persistence;

import io.reflectoring.buckpal.application.domain.model.Account;
import io.reflectoring.buckpal.application.domain.model.Account.AccountId;
import io.reflectoring.buckpal.application.domain.model.Activity;
import io.reflectoring.buckpal.application.domain.model.Activity.ActivityId;
import io.reflectoring.buckpal.application.domain.model.ActivityWindow;
import io.reflectoring.buckpal.application.domain.model.Money;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountMapperTest {

    private AccountMapper accountMapper;

    @BeforeEach
    void setUp() {
        accountMapper = new AccountMapper();
    }

    @Test
    void shouldMapToDomainEntity() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.of(2026, 10, 20, 10, 0);

        AccountJpaEntity accountJpa = new AccountJpaEntity(1L);

        ActivityJpaEntity activityJpa = new ActivityJpaEntity(
                10L,
                timestamp,
                1L,     // propietario
                2L,     // origen
                1L,     // destino
                500L    // monto
        );

        List<ActivityJpaEntity> activities = List.of(activityJpa);

        long withdrawalBalance = 200L;
        long depositBalance = 1500L;

        // Act
        Account result = accountMapper.mapToDomainEntity(
                accountJpa,
                activities,
                withdrawalBalance,
                depositBalance
        );

        // Assert, cuenta y saldo
        assertTrue(result.getId().isPresent());
        assertEquals(new AccountId(1L), result.getId().get());
        assertEquals(Money.of(1800), result.calculateBalance());

        // Assert, ventana de actividades
        ActivityWindow window = result.getActivityWindow();

        assertEquals(1, window.getActivities().size());

        Activity mappedActivity = window.getActivities().get(0);

        assertEquals(new ActivityId(10L), mappedActivity.getId());
        assertEquals(new AccountId(1L), mappedActivity.getOwnerAccountId());
        assertEquals(new AccountId(2L), mappedActivity.getSourceAccountId());
        assertEquals(new AccountId(1L), mappedActivity.getTargetAccountId());
        assertEquals(timestamp, mappedActivity.getTimestamp());
        assertEquals(Money.of(500), mappedActivity.getMoney());
    }

    @Test
    void shouldMapActivityToJpaEntity() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.of(2026, 10, 20, 11, 30);

        Activity activity = new Activity(
                new AccountId(1L),
                new AccountId(2L),
                new AccountId(1L),
                timestamp,
                Money.of(750)
        );

        // Act
        ActivityJpaEntity result = accountMapper.mapToJpaEntity(activity);

        // Assert
        assertNull(result.getId());
        assertEquals(timestamp, result.getTimestamp());
        assertEquals(1L, result.getOwnerAccountId());
        assertEquals(2L, result.getSourceAccountId());
        assertEquals(1L, result.getTargetAccountId());
        assertEquals(750L, result.getAmount());
    }
}
