package io.reflectoring.buckpal.adapter.out.persistence;

import io.reflectoring.buckpal.application.domain.model.Account;
import io.reflectoring.buckpal.application.domain.model.ActivityWindow;
import io.reflectoring.buckpal.application.domain.model.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AccountPersistenceAdapterTest {

    @Mock
    private SpringDataAccountRepository accountRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountPersistenceAdapter persistenceAdapter;

    @Test
    void loadAccountSuccess() {
        // 1. GIVEN
        Long accountIdValue = 42L;
        Account.AccountId targetAccountId = new Account.AccountId(accountIdValue);

        AccountJpaEntity accountJpaEntity = new AccountJpaEntity();
        accountJpaEntity.setId(accountIdValue);

        given(accountRepository.findById(accountIdValue))
                .willReturn(Optional.of(accountJpaEntity));

        given(activityRepository.findByOwnerSince(anyLong(), any(LocalDateTime.class)))
                .willReturn(List.of());

        // Ajustamos los parámetros para que coincidan con la firma real (pasando anyLong() donde pide Long)
        given(accountMapper.mapToDomainEntity(
                any(AccountJpaEntity.class),
                any(List.class),
                anyLong(),
                anyLong()))
                .willReturn(Account.withId(
                        targetAccountId,
                        Money.of(500L),
                        new ActivityWindow(List.of())
                ));

        // 2. WHEN
        Account account = persistenceAdapter.loadAccount(targetAccountId, LocalDateTime.now());

        // 3. THEN
        assertThat(account).isNotNull();
        assertThat(account.getId().get()).isEqualTo(targetAccountId);
    }
}