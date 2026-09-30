package restaurante.team3.giacobello.invoices.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import restaurante.team3.giacobello.infrastructure.IntegrationTest;
import restaurante.team3.giacobello.invoices.entity.InvoiceEntity;
import restaurante.team3.giacobello.invoices.repository.InvoiceRepository;
import restaurante.team3.giacobello.orders.entity.OrderEntity;
import restaurante.team3.giacobello.orders.repository.OrderRepository;

class InvoiceControllerIntegrationTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Test
    void findAllReturnsOkAndListOfInvoices() throws Exception {
        mockMvc.perform(get("/api/v1/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Transactional
    void findAllWithDateRangeReturnsOnlyInvoicesIssuedInsideRange() throws Exception {
        saveInvoice("INV-SEP", LocalDateTime.of(2026, 9, 30, 23, 45));
        saveInvoice("INV-OCT", LocalDateTime.of(2026, 10, 1, 0, 15));

        mockMvc.perform(get("/api/v1/invoices")
                .param("from", "2026-09-01")
                .param("to", "2026-09-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].invoiceNumber").value("INV-SEP"));
    }

    @Test
    void findAllWithStartDateAfterEndDateReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/invoices")
                .param("from", "2026-09-30")
                .param("to", "2026-09-01"))
                .andExpect(status().isBadRequest());
    }

    private void saveInvoice(String invoiceNumber, LocalDateTime issuedAt) {
        OrderEntity order = orderRepository.saveAndFlush(new OrderEntity(
                null, 1, "DINE IN", "CASH", "PENDING",
                new BigDecimal("20.00"),
                issuedAt));
        invoiceRepository.saveAndFlush(new InvoiceEntity(
                order.getId(),
                invoiceNumber,
                new BigDecimal("20.00"),
                issuedAt));
    }
}
