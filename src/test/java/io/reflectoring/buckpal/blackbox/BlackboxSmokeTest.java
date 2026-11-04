package io.reflectoring.buckpal.blackbox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BlackboxSmokeTest extends BaseSeleniumTest {

    @Test
    public void testBaseSetup() {
        assertNotNull(driver, "El WebDriver deberia estar inicializado");
    }
}