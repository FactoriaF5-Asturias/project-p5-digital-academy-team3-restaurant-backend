package restaurante.team3.giacobello.payment_method.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import restaurante.team3.giacobello.infrastructure.IntegrationTest;

class PaymentMethodControllerIntegrationTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void findAllReturnsPaymentMethodsSortedByName() throws Exception {
        mockMvc.perform(get("/api/v1/paymentmethod"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("CARD"))
                .andExpect(jsonPath("$[1].name").value("CASH"));
    }
}
