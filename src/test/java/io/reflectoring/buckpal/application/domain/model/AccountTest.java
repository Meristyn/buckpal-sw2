package io.reflectoring.buckpal.application.domain.model;

import io.reflectoring.buckpal.common.AccountTestData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;

class AccountTest {

    private Account account;

    // 1. SETUP: Se ejecuta automáticamente ANTES de cada prueba
    @BeforeEach
    void setUp() {
        account = AccountTestData.defaultAccount()
                .withBaselineBalance(Money.of(500L))
                .withActivityWindow(new ActivityWindow(new ArrayList<>()))
                .build();
    }

    // 2. TEAR_DOWN: Se ejecuta automáticamente DESPUÉS de cada prueba
    @AfterEach
    void tearDown() {
        account = null;
    }

    // 3. TEST CASES Y ASSERTIONS (Casos de prueba y validaciones)
    @Test
    void testCalculateBalance() {
        Money balance = account.calculateBalance();

        // Assertions: Comprobamos que el saldo sea 500
        assertThat(balance).isEqualTo(Money.of(500L));
    }

    @Test
    void testDepositSucceeds() {
        boolean success = account.deposit(Money.of(150L), new Account.AccountId(99L));

        // Assertions: Comprobamos que el depósito fue exitoso y subió a 650
        assertThat(success).isTrue();
        assertThat(account.calculateBalance()).isEqualTo(Money.of(650L));
    }
}