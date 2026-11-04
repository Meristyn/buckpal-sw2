package io.reflectoring.buckpal.adapter.out.persistence;

import io.reflectoring.buckpal.application.domain.model.Account;
import io.reflectoring.buckpal.application.domain.model.Activity;
import io.reflectoring.buckpal.application.domain.model.Activity.ActivityId;
import io.reflectoring.buckpal.application.domain.model.ActivityWindow;
import io.reflectoring.buckpal.application.domain.model.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.reflectoring.buckpal.common.AccountTestData.defaultAccount;
import static io.reflectoring.buckpal.common.ActivityTestData.defaultActivity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountPersistenceUpdateActivitiesTest {

    @Mock
    private SpringDataAccountRepository accountRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountPersistenceAdapter adapterUnderTest;

    @Test
    void updatesOnlyNewActivitiesWithNullId() {
        // Arrange (Inicializar)
        Activity existingActivity = defaultActivity()
                .withId(new ActivityId(42L))
                .withMoney(Money.of(100))
                .build();

        Activity newActivity = defaultActivity()
                .withId(null)
                .withMoney(Money.of(500))
                .build();

        Account account = defaultAccount()
                .withActivityWindow(new ActivityWindow(existingActivity, newActivity))
                .build();

        ActivityJpaEntity mappedJpaEntity = new ActivityJpaEntity();
        when(accountMapper.mapToJpaEntity(newActivity)).thenReturn(mappedJpaEntity);

        // Act (Ejecutar)
        adapterUnderTest.updateActivities(account);

        // Assert (Verificar)
        verify(accountMapper, never()).mapToJpaEntity(existingActivity);
        verify(accountMapper, times(1)).mapToJpaEntity(newActivity);
        verify(activityRepository, times(1)).save(mappedJpaEntity);
    }
}