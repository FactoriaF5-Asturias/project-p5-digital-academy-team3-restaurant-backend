package restaurante.team3.giacobello.invoices.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
    void adminCanFindAllInvoices() throws Exception {
        mockMvc.perform(get("/api/v1/invoices")
            .header("Authorization", "Bearer " + tokenForRole("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void clientCannotFindAllInvoices() throws Exception {
        mockMvc.perform(get("/api/v1/invoices")
                .header("Authorization", "Bearer " + tokenForRole("CLIENT")))
                .andExpect(status().isForbidden());
    }
    
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
            .header("Authorization", "Bearer " + tokenForRole("ADMIN"))
                .param("from", "2026-09-30")
                .param("to", "2026-09-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllWithOnlyOneDateReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/invoices")
        .header("Authorization", "Bearer " + tokenForRole("ADMIN"))
                .param("from", "2026-09-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void salesTotalsReturnsSumsOfDayMonthQuarterAndYearOfGivenDate() throws Exception {
        saveInvoice("INV-DAY", LocalDateTime.of(2031, 8, 15, 10, 0), new BigDecimal("10.00"));
        saveInvoice("INV-MONTH", LocalDateTime.of(2031, 8, 3, 13, 30), new BigDecimal("20.00"));
        saveInvoice("INV-QUARTER", LocalDateTime.of(2031, 7, 20, 21, 0), new BigDecimal("40.00"));
        saveInvoice("INV-YEAR", LocalDateTime.of(2031, 2, 10, 14, 0), new BigDecimal("80.00"));
        saveInvoice("INV-LAST-YEAR", LocalDateTime.of(2030, 12, 31, 23, 59), new BigDecimal("160.00"));

        mockMvc.perform(get("/api/v1/invoices/totals")
            .header("Authorization", "Bearer " + tokenForRole("ADMIN"))
                .param("date", "2031-08-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.daily").value(10.0))
                .andExpect(jsonPath("$.monthly").value(30.0))
                .andExpect(jsonPath("$.quarterly").value(70.0))
                .andExpect(jsonPath("$.yearly").value(150.0))
                .andExpect(jsonPath("$.dailyOrders").value(1))
                .andExpect(jsonPath("$.monthlyOrders").value(2))
                .andExpect(jsonPath("$.quarterlyOrders").value(3))
                .andExpect(jsonPath("$.yearlyOrders").value(4));
    }

    @Test
    void salesTotalsReturnsZeroWhenThereAreNoSales() throws Exception {
        mockMvc.perform(get("/api/v1/invoices/totals")
            .header("Authorization", "Bearer " + tokenForRole("ADMIN"))
                .param("date", "2035-06-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.daily").value(0))
                .andExpect(jsonPath("$.monthly").value(0))
                .andExpect(jsonPath("$.quarterly").value(0))
                .andExpect(jsonPath("$.yearly").value(0))
                .andExpect(jsonPath("$.dailyOrders").value(0))
                .andExpect(jsonPath("$.monthlyOrders").value(0))
                .andExpect(jsonPath("$.quarterlyOrders").value(0))
                .andExpect(jsonPath("$.yearlyOrders").value(0));
    }

    @Test
    @Transactional
    void salesReportPdfReturnsPdfWithInvoicesOfTheRequestedQuarter() throws Exception {
        saveInvoice("INV-Q3-JUL", LocalDateTime.of(2032, 7, 1, 9, 0));
        saveInvoice("INV-Q3-SEP", LocalDateTime.of(2032, 9, 30, 23, 30));
        saveInvoice("INV-Q4-OCT", LocalDateTime.of(2032, 10, 1, 0, 30));

        MvcResult result = mockMvc.perform(get("/api/v1/invoices/report/pdf")
            .header("Authorization", "Bearer " + tokenForRole("ADMIN"))
                .param("period", "QUARTER")
                .param("date", "2032-08-15"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"ventas-2032-07-01_2032-09-30.pdf\""))
                .andReturn();

        String text = extractText(result.getResponse().getContentAsByteArray());
        assertTrue(text.contains("01/07/2032 - 30/09/2032"));
        assertTrue(text.contains("INV-Q3-JUL"));
        assertTrue(text.contains("INV-Q3-SEP"));
        assertFalse(text.contains("INV-Q4-OCT"));
        assertTrue(text.contains("Pedidos: 2"));
    }

    @Test
    void salesReportPdfWithoutDateUsesToday() throws Exception {
        mockMvc.perform(get("/api/v1/invoices/report/pdf")
            .header("Authorization", "Bearer " + tokenForRole("ADMIN"))
                .param("period", "DAY"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    void salesReportPdfExposesContentDispositionToTheFrontend() throws Exception {
        mockMvc.perform(get("/api/v1/invoices/report/pdf")
                .header("Authorization", "Bearer " + tokenForRole("ADMIN"))
                .param("period", "DAY")
                .header(HttpHeaders.ORIGIN, "http://localhost:5173"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                        HttpHeaders.CONTENT_DISPOSITION));
    }

    @Test
    void salesReportPdfWithUnknownPeriodReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/invoices/report/pdf")
            .header("Authorization", "Bearer " + tokenForRole("ADMIN"))
                .param("period", "WEEK"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void salesReportPdfWithoutPeriodReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/invoices/report/pdf")
                .header("Authorization", "Bearer " + tokenForRole("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    private String extractText(byte[] pdf) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdf)) {
            return new PDFTextStripper().getText(document);
        }
    }

    private void saveInvoice(String invoiceNumber, LocalDateTime issuedAt) {
        saveInvoice(invoiceNumber, issuedAt, new BigDecimal("20.00"));
    }

    private void saveInvoice(String invoiceNumber, LocalDateTime issuedAt, BigDecimal totalAmount) {
        OrderEntity order = orderRepository.saveAndFlush(new OrderEntity(
                null, 1, "DINE IN", "CASH", "PENDING",
                totalAmount,
                issuedAt));
        invoiceRepository.saveAndFlush(new InvoiceEntity(
                order.getId(),
                invoiceNumber,
                totalAmount,
                issuedAt));
    }
}
