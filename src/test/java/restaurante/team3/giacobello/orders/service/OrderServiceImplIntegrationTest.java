package restaurante.team3.giacobello.orders.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import restaurante.team3.giacobello.infrastructure.IntegrationTest;
import restaurante.team3.giacobello.orders.dto.OrderCreateDTORequest;
import restaurante.team3.giacobello.orders.dto.OrderDTOResponse;
import restaurante.team3.giacobello.orders.dto.OrderItemCreateDTORequest;

class OrderServiceImplIntegrationTest extends IntegrationTest {

    @Autowired
    private OrderService orderService;

    @Test
    @Transactional
    void createReturnsPendingOrderWithTotalAmount() {
        OrderCreateDTORequest request = new OrderCreateDTORequest(
                3,
                "DINE IN",
                "CASH",
                List.of(
                        new OrderItemCreateDTORequest(1, 2),
                        new OrderItemCreateDTORequest(8, 1)));

        OrderDTOResponse response = orderService.create(request);

        assertEquals(3, response.tabletId());
        assertEquals("DINE IN", response.orderTypeName());
        assertEquals("CASH", response.paymentMethodName());
        assertEquals("PENDING", response.statusName());
        assertEquals(new BigDecimal("33.00"), response.totalAmount());
        assertEquals(2, response.items().size());
        assertNotNull(response.createdAt());
    }
}
