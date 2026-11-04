
package io.reflectoring.buckpal.application.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    // SUMA: plus()

    @Test
    void shouldAddMoneyUsingPlus() {
        Money first = Money.of(100);
        Money second = Money.of(50);

        Money result = first.plus(second);

        assertEquals(Money.of(150), result);
    }

    // SUMA: add()

    @Test
    void shouldAddMoneyUsingAdd() {
        Money first = Money.of(100);
        Money second = Money.of(50);

        Money result = Money.add(first, second);

        assertEquals(Money.of(150), result);
    }

    // RESTA: minus()

    @Test
    void shouldSubtractMoneyUsingMinus() {
        Money first = Money.of(100);
        Money second = Money.of(30);

        Money result = first.minus(second);

        assertEquals(Money.of(70), result);
    }

    // RESTA: subtract()

    @Test
    void shouldSubtractMoneyUsingSubtract() {
        Money first = Money.of(100);
        Money second = Money.of(30);

        Money result = Money.subtract(first, second);

        assertEquals(Money.of(70), result);
    }

    // NEGACIÓN

    @Test
    void shouldNegatePositiveMoney() {
        Money result = Money.of(100).negate();

        assertEquals(Money.of(-100), result);
    }

    @Test
    void shouldNegateNegativeMoney() {
        Money result = Money.of(-50).negate();

        assertEquals(Money.of(50), result);
    }

    @Test
    void shouldKeepZeroWhenNegated() {
        Money result = Money.ZERO.negate();

        assertEquals(Money.ZERO, result);
    }

    // COMPARACIONES LÓGICAS

    @Test
    void shouldIdentifyPositiveMoney() {
        assertTrue(Money.of(10).isPositive());
        assertFalse(Money.ZERO.isPositive());
        assertFalse(Money.of(-10).isPositive());
    }

    @Test
    void shouldIdentifyNegativeMoney() {
        assertTrue(Money.of(-10).isNegative());
        assertFalse(Money.ZERO.isNegative());
        assertFalse(Money.of(10).isNegative());
    }

    @Test
    void shouldIdentifyPositiveOrZeroMoney() {
        assertTrue(Money.of(10).isPositiveOrZero());
        assertTrue(Money.ZERO.isPositiveOrZero());
        assertFalse(Money.of(-10).isPositiveOrZero());
    }

    @Test
    void shouldIdentifyGreaterMoney() {
        assertTrue(Money.of(100).isGreaterThan(Money.of(50)));
        assertFalse(Money.of(50).isGreaterThan(Money.of(100)));
        assertFalse(Money.of(50).isGreaterThan(Money.of(50)));
    }

    @Test
    void shouldIdentifyGreaterThanOrEqualMoney() {
        assertTrue(Money.of(100).isGreaterThanOrEqualTo(Money.of(50)));
        assertTrue(Money.of(50).isGreaterThanOrEqualTo(Money.of(50)));
        assertFalse(Money.of(40).isGreaterThanOrEqualTo(Money.of(50)));
    }
}
