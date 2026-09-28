package restaurante.team3.giacobello.orders.controller;

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
import restaurante.team3.giacobello.orders.entity.OrderEntity;
import restaurante.team3.giacobello.orders.repository.OrderItemRepository;
import restaurante.team3.giacobello.orders.repository.OrderRepository;

class OrderControllerIntegrationTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

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

    private OrderEntity saveOrder(String statusName) {
        OrderEntity order = new OrderEntity(
                null, 1, "DINE IN", "CASH", statusName,
                new BigDecimal("120.00"),
                LocalDateTime.of(2026, 9, 28, 12, 30));

        return orderRepository.saveAndFlush(order);
    }

    private void deleteOrders() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }
}