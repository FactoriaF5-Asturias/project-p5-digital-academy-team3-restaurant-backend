package restaurante.team3.giacobello.orders.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import restaurante.team3.giacobello.invoices.repository.InvoiceRepository;
import restaurante.team3.giacobello.orders.entity.OrderEntity;
import restaurante.team3.giacobello.orders.entity.OrderItemEntity;
import restaurante.team3.giacobello.orders.repository.OrderItemRepository;
import restaurante.team3.giacobello.orders.repository.OrderRepository;

import restaurante.team3.giacobello.infrastructure.IntegrationTest;
import restaurante.team3.giacobello.orders.dto.OrderCreateDTORequest;
import restaurante.team3.giacobello.orders.dto.OrderDTOResponse;
import restaurante.team3.giacobello.orders.dto.OrderItemCreateDTORequest;

class OrderServiceImplIntegrationTest extends IntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Test
    @Transactional
    void createSavesOrderItemsWithoutInvoice() {
        deleteOrders();
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

        OrderEntity savedOrder = orderRepository.findById(response.id()).orElseThrow();
        List<OrderItemEntity> savedItems = orderItemRepository.findAll();

        assertEquals(3, savedOrder.getTabletId());
        assertEquals("DINE IN", savedOrder.getOrderTypeName());
        assertEquals("CASH", savedOrder.getPaymentMethodName());
        assertEquals("PENDING", savedOrder.getStatusName());
        assertEquals(new BigDecimal("33.00"), savedOrder.getTotalAmount());
        assertNotNull(savedOrder.getCreatedAt());
        assertNull(savedOrder.getPaidAt());

        assertEquals(2, savedItems.size());

        assertTrue(invoiceRepository.findByOrderId(response.id()).isEmpty());
    }

    private void deleteOrders() {
        invoiceRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }
}
