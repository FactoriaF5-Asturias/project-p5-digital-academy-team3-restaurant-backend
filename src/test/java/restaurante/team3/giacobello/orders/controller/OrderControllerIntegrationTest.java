package restaurante.team3.giacobello.orders.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import restaurante.team3.giacobello.infrastructure.IntegrationTest;
import restaurante.team3.giacobello.orders.entity.OrderEntity;
import restaurante.team3.giacobello.orders.repository.OrderItemRepository;
import restaurante.team3.giacobello.orders.repository.OrderRepository;
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
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    @Transactional
    void findByIdReturnsOrder() throws Exception {
        deleteOrders();
        OrderEntity order = saveOrder("ACCEPTED");
        mockMvc.perform(get("/api/v1/orders/{id}", order.getId()))
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

        mockMvc.perform(get("/api/v1/orders/{id}", order.getId()))
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
                .servletPath("/api/v1/orders/" + order.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.statusName").value("COMPLETED"));
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