package restaurante.team3.giacobello.payments.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import restaurante.team3.giacobello.infrastructure.IntegrationTest;
import restaurante.team3.giacobello.payments.exceptions.PaymentGatewayException;
import restaurante.team3.giacobello.payments.exceptions.PaymentsNotConfiguredException;
import restaurante.team3.giacobello.payments.gateway.PaymentIntentCreated;
import restaurante.team3.giacobello.payments.gateway.StripePaymentGateway;

@ActiveProfiles({ "test", "dev" })
class PaymentControllerIntegrationTest extends IntegrationTest {

    private static final String URL = "/api/v1/payments/create-intent";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StripePaymentGateway paymentGateway;

    private String body(String items) {
        return "{ \"items\": " + items + " }";
    }

    @Test
    void createIntentReturnsTheAmountComputedFromDatabasePricesWithoutToken() throws Exception {
        when(paymentGateway.createPaymentIntent(3450L, "eur"))
                .thenReturn(new PaymentIntentCreated("pi_test_1", "pi_test_1_secret"));

        mockMvc.perform(post(URL)
                .servletPath(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("[ { \"productId\": 1, \"quantity\": 2 }, { \"productId\": 2, \"quantity\": 1 } ]")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentIntentId").value("pi_test_1"))
                .andExpect(jsonPath("$.clientSecret").value("pi_test_1_secret"))
                .andExpect(jsonPath("$.amount").value(34.5));

        verify(paymentGateway).createPaymentIntent(3450L, "eur");
    }

    @Test
    void createIntentRejectsEmptyItems() throws Exception {
        mockMvc.perform(post(URL)
                .servletPath(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("[]")))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(paymentGateway);
    }

    @Test
    void createIntentRejectsInvalidQuantity() throws Exception {
        mockMvc.perform(post(URL)
                .servletPath(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("[ { \"productId\": 1, \"quantity\": 0 } ]")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createIntentRejectsUnknownProduct() throws Exception {
        mockMvc.perform(post(URL)
                .servletPath(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("[ { \"productId\": 99999, \"quantity\": 1 } ]")))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(paymentGateway);
    }

    @Test
    void createIntentAnswers503WhenStripeIsNotConfigured() throws Exception {
        when(paymentGateway.createPaymentIntent(anyLong(), anyString()))
                .thenThrow(new PaymentsNotConfiguredException());

        mockMvc.perform(post(URL)
                .servletPath(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("[ { \"productId\": 1, \"quantity\": 1 } ]")))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value(
                        "Los pagos con tarjeta no están configurados: falta STRIPE_SECRET_KEY"));
    }

    @Test
    void createIntentAnswers502WhenStripeFails() throws Exception {
        when(paymentGateway.createPaymentIntent(anyLong(), anyString()))
                .thenThrow(new PaymentGatewayException(new RuntimeException("secret detail")));

        mockMvc.perform(post(URL)
                .servletPath(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("[ { \"productId\": 1, \"quantity\": 1 } ]")))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.message").value("No se pudo contactar con el proveedor de pagos"));
    }
}
