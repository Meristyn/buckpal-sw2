package io.reflectoring.buckpal.adapter.in.web;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.reflectoring.buckpal.application.domain.model.Account.AccountId;
import io.reflectoring.buckpal.application.domain.model.Money;
import io.reflectoring.buckpal.application.port.in.SendMoneyCommand;
import io.reflectoring.buckpal.application.port.in.SendMoneyUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SendMoneyController.class)
class SendMoneyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SendMoneyUseCase sendMoneyUseCase;

    @Test
    void sendMoneyInvocaElCasoDeUsoConElComandoCorrecto() throws Exception {
        // Arrange
        when(sendMoneyUseCase.sendMoney(eq(new SendMoneyCommand(
                new AccountId(41L), new AccountId(42L), Money.of(500L))))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/accounts/send/41/42/500"))
                .andExpect(status().isOk());

        // Assert
        then(sendMoneyUseCase).should().sendMoney(new SendMoneyCommand(
                new AccountId(41L), new AccountId(42L), Money.of(500L)));
    }
}