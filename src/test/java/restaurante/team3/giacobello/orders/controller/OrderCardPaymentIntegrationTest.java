package restaurante.team3.giacobello.orders.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

import restaurante.team3.giacobello.infrastructure.IntegrationTest;
import restaurante.team3.giacobello.invoices.entity.InvoiceEntity;
import restaurante.team3.giacobello.invoices.repository.InvoiceRepository;
import restaurante.team3.giacobello.orders.entity.OrderEntity;
import restaurante.team3.giacobello.orders.repository.OrderRepository;
import restaurante.team3.giacobello.payments.exceptions.PaymentsNotConfiguredException;
import restaurante.team3.giacobello.payments.gateway.PaymentIntentDetails;
import restaurante.team3.giacobello.payments.gateway.StripePaymentGateway;

@ActiveProfiles({ "test", "dev" })
class OrderCardPaymentIntegrationTest extends IntegrationTest {

    private static final String URL = "/api/v1/orders";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @MockitoBean
    private StripePaymentGateway paymentGateway;

    private String body(String paymentMethodName, String paymentIntentId) {
        String intent = paymentIntentId == null ? "" : ", \"paymentIntentId\": \"" + paymentIntentId + "\"";
        return """
                {
                  "tabletId": 3,
                  "orderTypeName": "DINE IN",
                  "paymentMethodName": "%s",
                  "items": [ { "productId": 1, "quantity": 2 }, { "productId": 2, "quantity": 1 } ]%s
                }
                """.formatted(paymentMethodName, intent);
    }

    private org.springframework.test.web.servlet.ResultActions placeOrder(String body) throws Exception {
        return mockMvc.perform(post(URL)
                .servletPath(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    @Test
    @Transactional
    void createOrderWithSucceededPaymentIntentIsPaidAndInvoiced() throws Exception {
        when(paymentGateway.retrievePaymentIntent("pi_ok"))
                .thenReturn(new PaymentIntentDetails("pi_ok", "succeeded", 3450, "eur"));

        MvcResult result = placeOrder(body("CARD", "pi_ok"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusName").value("PENDING"))
                .andExpect(jsonPath("$.paidAt").isNotEmpty())
                .andExpect(jsonPath("$.totalAmount").value(34.5))
                .andReturn();

        Integer orderId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        InvoiceEntity invoice = invoiceRepository.findByOrderId(orderId).orElseThrow();
        assertEquals("INV-" + orderId, invoice.getInvoiceNumber());
        assertEquals(new BigDecimal("34.50"), invoice.getTotalAmount());
        assertTrue(orderRepository.existsByStripePaymentIntentId("pi_ok"));
    }

    @Test
    @Transactional
    void reusingThePaymentIntentIsRejected() throws Exception {
        when(paymentGateway.retrievePaymentIntent("pi_once"))
                .thenReturn(new PaymentIntentDetails("pi_once", "succeeded", 3450, "eur"));

        placeOrder(body("CARD", "pi_once")).andExpect(status().isCreated());
        placeOrder(body("CARD", "pi_once")).andExpect(status().isConflict());
    }

    @Test
    @Transactional
    void paymentIntentNotSucceededIsRejectedWithPaymentRequired() throws Exception {
        when(paymentGateway.retrievePaymentIntent("pi_pending"))
                .thenReturn(new PaymentIntentDetails("pi_pending", "requires_payment_method", 3450, "eur"));

        placeOrder(body("CARD", "pi_pending")).andExpect(status().isPaymentRequired());
    }

    @Test
    @Transactional
    void paidAmountMismatchIsRejected() throws Exception {
        when(paymentGateway.retrievePaymentIntent("pi_low"))
                .thenReturn(new PaymentIntentDetails("pi_low", "succeeded", 100, "eur"));

        placeOrder(body("CARD", "pi_low")).andExpect(status().isConflict());
    }

    @Test
    @Transactional
    void paymentIntentWithCashIsRejected() throws Exception {
        placeOrder(body("CASH", "pi_cash")).andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void paymentIntentWithoutStripeConfiguredAnswers503() throws Exception {
        when(paymentGateway.retrievePaymentIntent("pi_x")).thenThrow(new PaymentsNotConfiguredException());

        placeOrder(body("CARD", "pi_x")).andExpect(status().isServiceUnavailable());
    }

    @Test
    @Transactional
    void cardOrderWithoutPaymentIntentStaysUnpaid() throws Exception {
        placeOrder(body("CARD", null))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paidAt").doesNotExist());
    }

    @Test
    @Transactional
    void databaseRejectsTwoOrdersWithTheSamePaymentIntent() {
        orderRepository.saveAndFlush(orderWithIntent("pi_dup"));

        assertThrows(DataIntegrityViolationException.class,
                () -> orderRepository.saveAndFlush(orderWithIntent("pi_dup")));
    }

    private OrderEntity orderWithIntent(String intentId) {
        OrderEntity order = new OrderEntity(
                null, 1, "DINE IN", "CARD", "PENDING",
                new BigDecimal("10.00"), LocalDateTime.of(2026, 10, 5, 12, 0));
        order.setStripePaymentIntentId(intentId);
        return order;
    }
}
