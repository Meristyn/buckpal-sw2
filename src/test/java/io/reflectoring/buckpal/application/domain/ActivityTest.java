package io.reflectoring.buckpal.application.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import io.reflectoring.buckpal.application.domain.model.Account.AccountId;
import io.reflectoring.buckpal.application.domain.model.Activity;
import io.reflectoring.buckpal.application.domain.model.Money;

class ActivityTest {

    @Test
    void creaActividadConLosValoresIndicados() {
        // Arrange
        AccountId owner = new AccountId(1L);
        AccountId source = new AccountId(1L);
        AccountId target = new AccountId(2L);
        LocalDateTime ts = LocalDateTime.of(2026, 10, 26, 10, 0);
        Money money = Money.of(500L);

        // Act
        Activity activity = new Activity(owner, source, target, ts, money);

        // Assert
        assertThat(activity.getOwnerAccountId()).isEqualTo(owner);
        assertThat(activity.getSourceAccountId()).isEqualTo(source);
        assertThat(activity.getTargetAccountId()).isEqualTo(target);
        assertThat(activity.getTimestamp()).isEqualTo(ts);
        assertThat(activity.getMoney()).isEqualTo(Money.of(500L));
    }

    @Test
    void dosAccountIdConElMismoValorSonIguales() {
        assertThat(new AccountId(42L)).isEqualTo(new AccountId(42L));
        assertThat(new AccountId(42L).hashCode()).isEqualTo(new AccountId(42L).hashCode());
    }
}