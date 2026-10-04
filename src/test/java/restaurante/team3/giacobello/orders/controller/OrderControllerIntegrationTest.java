package restaurante.team3.giacobello.orders.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import restaurante.team3.giacobello.infrastructure.IntegrationTest;
import restaurante.team3.giacobello.orders.entity.OrderEntity;
import restaurante.team3.giacobello.orders.repository.OrderItemRepository;
import restaurante.team3.giacobello.orders.repository.OrderRepository;
import restaurante.team3.giacobello.invoices.entity.InvoiceEntity;
import restaurante.team3.giacobello.invoices.repository.InvoiceRepository;

@ActiveProfiles({ "test", "dev" })
class OrderControllerIntegrationTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Test
    @Transactional
    void findAllReturnsOkAndListOfOrders() throws Exception {
        deleteOrders();
        saveOrder("PENDING");
        mockMvc.perform(get("/api/v1/orders")
          .header("Authorization", "Bearer " + tokenForRole("KITCHEN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    @Transactional
    void findByIdReturnsOrder() throws Exception {
        deleteOrders();
        OrderEntity order = saveOrder("ACCEPTED");
        mockMvc.perform(get("/api/v1/orders/{id}", order.getId())
          .header("Authorization", "Bearer " + tokenForRole("KITCHEN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.statusName").value("ACCEPTED"));
    }

    @Test
    @Transactional
    void findByIdReturnsPaidAtWhenOrderIsPaid() throws Exception {
        deleteOrders();
        OrderEntity order = saveOrder("COMPLETED");
        order.setPaidAt(LocalDateTime.of(2026, 9, 28, 13, 15));
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get("/api/v1/orders/{id}", order.getId())
                .header("Authorization", "Bearer " + tokenForRole("KITCHEN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paidAt").value("2026-09-28T13:15:00"));
    }

    @Test
    @Transactional
    void createReturnsCreatedOrder() throws Exception {
        deleteOrders();
        String requestBody = """
                {
                  "tabletId": 3,
                  "orderTypeName": "DINE IN",
                  "paymentMethodName": "CASH",
                  "items": [
                    { "productId": 1, "quantity": 2 },
                    { "productId": 8, "quantity": 1 }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/orders")
                .servletPath("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.tabletId").value(3))
                .andExpect(jsonPath("$.statusName").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(33.0))
                .andExpect(jsonPath("$.items.length()").value(2));
    }

    @Test
    @Transactional
    void updateStatusReturnsUpdatedOrder() throws Exception {
        deleteOrders();
        OrderEntity order = saveOrder("PENDING");
        String requestBody = """
                {
                  "statusName": "completed"
                }
                """;

        mockMvc.perform(put("/api/v1/orders/{id}/status", order.getId())
          .header("Authorization", "Bearer " + tokenForRole("KITCHEN"))
                .servletPath("/api/v1/orders/" + order.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.statusName").value("COMPLETED"));
    }

                @Test
                @Transactional
                void kitchenCanRejectPendingOrderOnlyOnce() throws Exception {
              deleteOrders();
              OrderEntity order = saveOrder("PENDING");
              String requestBody = "{\"statusName\":\"CANCELLED\"}";

              mockMvc.perform(put("/api/v1/orders/{id}/status", order.getId())
                .header("Authorization", "Bearer " + tokenForRole("KITCHEN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusName").value("CANCELLED"));

              mockMvc.perform(put("/api/v1/orders/{id}/status", order.getId())
                .header("Authorization", "Bearer " + tokenForRole("KITCHEN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isConflict());
                }
    @Test
    @Transactional
    void payMarksOrderAsPaidAndIssuesItsInvoice() throws Exception {
        deleteOrders();
        OrderEntity order = saveOrder("COMPLETED");

        mockMvc.perform(payRequest(order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.statusName").value("COMPLETED"))
                .andExpect(jsonPath("$.paidAt").isNotEmpty());

        InvoiceEntity invoice = invoiceRepository.findByOrderId(order.getId()).orElseThrow();
        assertEquals("INV-" + order.getId(), invoice.getInvoiceNumber());
        assertEquals(new BigDecimal("120.00"), invoice.getTotalAmount());
    }

    @Test
    @Transactional
    void payReturnsConflictWhenOrderIsAlreadyPaid() throws Exception {
        deleteOrders();
        OrderEntity order = saveOrder("COMPLETED");

        mockMvc.perform(payRequest(order.getId()))
                .andExpect(status().isOk());
        mockMvc.perform(payRequest(order.getId()))
                .andExpect(status().isConflict());
    }

    @Test
    @Transactional
    void payReturnsNotFoundWhenOrderDoesNotExist() throws Exception {
        deleteOrders();

        mockMvc.perform(payRequest(999999))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void invoicesListOnlyPaidOrders() throws Exception {
        deleteOrders();
        String createdOrder = mockMvc.perform(post("/api/v1/orders")
                .servletPath("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "tabletId": 3,
                          "orderTypeName": "DINE IN",
                          "paymentMethodName": "CASH",
                          "items": [ { "productId": 1, "quantity": 1 } ]
                        }
                        """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Integer orderId = JsonPath.read(createdOrder, "$.id");

        mockMvc.perform(get("/api/v1/invoices")
                .header("Authorization", "Bearer " + tokenForRole("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(payRequest(orderId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/invoices")
                .header("Authorization", "Bearer " + tokenForRole("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderId").value(orderId));
    }

    @Test
    @Transactional
    void invoicePdfReturnsThePdfOfAPaidOrder() throws Exception {
        deleteOrders();
        String createdOrder = mockMvc.perform(post("/api/v1/orders")
                .servletPath("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "tabletId": 3,
                          "orderTypeName": "DINE IN",
                          "paymentMethodName": "CASH",
                          "items": [ { "productId": 1, "quantity": 2 } ]
                        }
                        """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Integer orderId = JsonPath.read(createdOrder, "$.id");
        String productName = JsonPath.read(createdOrder, "$.items[0].productName");
        mockMvc.perform(payRequest(orderId)).andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/api/v1/orders/{id}/invoice/pdf", orderId)
                .header("Authorization", "Bearer " + tokenForRole("KITCHEN")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"factura-pedido-" + orderId + ".pdf\""))
                .andReturn();

        String text = extractText(result.getResponse().getContentAsByteArray());
        assertTrue(text.contains("Factura: INV-" + orderId));
        assertTrue(text.contains(productName));
    }

    @Test
    @Transactional
    void invoicePdfReturnsNotFoundWhenOrderHasNoInvoice() throws Exception {
        deleteOrders();
        OrderEntity order = saveOrder("PENDING");

        mockMvc.perform(get("/api/v1/orders/{id}/invoice/pdf", order.getId())
                .header("Authorization", "Bearer " + tokenForRole("KITCHEN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void invoicePdfReturnsNotFoundWhenOrderDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/orders/{id}/invoice/pdf", 999999)
                .header("Authorization", "Bearer " + tokenForRole("KITCHEN")))
                .andExpect(status().isNotFound());
    }

    private String extractText(byte[] pdf) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdf)) {
            return new PDFTextStripper().getText(document);
        }
    }

    private MockHttpServletRequestBuilder payRequest(Integer id) {
        return put("/api/v1/orders/{id}/pay", id)
                .servletPath("/api/v1/orders/" + id + "/pay")
                .header("Authorization", "Bearer " + tokenForRole("ADMIN"));
    }

    private OrderEntity saveOrder(String statusName) {
        OrderEntity order = new OrderEntity(
                null, 1, "DINE IN", "CASH", statusName,
                new BigDecimal("120.00"),
                LocalDateTime.of(2026, 9, 28, 12, 30));
        return orderRepository.saveAndFlush(order);
    }

    private void deleteOrders() {
        invoiceRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }
}