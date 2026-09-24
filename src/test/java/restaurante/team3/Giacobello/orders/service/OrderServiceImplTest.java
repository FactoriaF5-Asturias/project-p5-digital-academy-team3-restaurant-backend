package restaurante.team3.giacobello.orders.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import restaurante.team3.giacobello.invoices.entity.InvoiceEntity;
import restaurante.team3.giacobello.invoices.repository.InvoiceRepository;
import restaurante.team3.giacobello.orders.dto.OrderCreateDTORequest;
import restaurante.team3.giacobello.orders.dto.OrderDTOResponse;
import restaurante.team3.giacobello.orders.dto.OrderItemCreateDTORequest;
import restaurante.team3.giacobello.orders.entity.OrderEntity;
import restaurante.team3.giacobello.orders.mappers.OrderMapper;
import restaurante.team3.giacobello.orders.repository.OrderItemRepository;
import restaurante.team3.giacobello.orders.repository.OrderRepository;
import restaurante.team3.giacobello.orders.service.OrderServiceImpl;
import restaurante.team3.giacobello.product.entity.ProductEntity;
import restaurante.team3.giacobello.product.repository.ProductRepository;
import restaurante.team3.giacobello.tablets.repository.TabletRepository;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

        @Mock
        private OrderRepository orderRepository;

        @Mock
        private OrderItemRepository orderItemRepository;

        @Mock
        private OrderMapper orderMapper;

        @Mock
        private ProductRepository productRepository;

        @Mock
        private TabletRepository tabletRepository;

        @Mock
        private InvoiceRepository invoiceRepository;

        @InjectMocks
        private OrderServiceImpl orderService;

        @Test
        void shouldCreateInvoiceWhenOrderIsCreated() {
                ProductEntity product = org.mockito.Mockito.mock(ProductEntity.class);
                OrderCreateDTORequest request = new OrderCreateDTORequest(
                                2,
                                "DINE IN",
                                "CASH",
                                List.of(new OrderItemCreateDTORequest(4, 2)));
                OrderDTOResponse response = new OrderDTOResponse(
                                7,
                                2,
                                "DINE IN",
                                "CASH",
                                "PENDING",
                                new java.math.BigDecimal("25.00"),
                                null,
                                List.of());

                when(tabletRepository.existsById(2)).thenReturn(true);
                when(productRepository.findAllById(List.of(4))).thenReturn(List.of(product));
                when(product.getId()).thenReturn(4);
                when(product.getPrice()).thenReturn(new java.math.BigDecimal("12.50"));
                when(product.getStatus()).thenReturn(true);
                doAnswer(invocation -> {
                        OrderEntity order = invocation.getArgument(0);
                        order.setId(7);
                        return order;
                }).when(orderRepository).save(any(OrderEntity.class));
                when(orderMapper.toResponse(any(OrderEntity.class))).thenReturn(response);

                orderService.create(request);

                ArgumentCaptor<InvoiceEntity> invoiceCaptor = ArgumentCaptor.forClass(InvoiceEntity.class);
                verify(invoiceRepository).save(invoiceCaptor.capture());
                InvoiceEntity invoice = invoiceCaptor.getValue();
                assertEquals(7, invoice.getOrderId());
                assertEquals("INV-7", invoice.getInvoiceNumber());
                assertEquals(new java.math.BigDecimal("25.00"), invoice.getTotalAmount());
                org.junit.jupiter.api.Assertions.assertNotNull(invoice.getIssuedAt());
        }

        @Test
        void shouldReturnAllOrders() {
                OrderEntity order = new OrderEntity();
                OrderDTOResponse response = new OrderDTOResponse(
                                1,
                                2,
                                "DINE_IN",
                                "CARD",
                                "IN PROGRESS",
                                null,
                                null,
                                List.of());

                when(orderRepository.findAll()).thenReturn(List.of(order));
                when(orderMapper.toResponse(order)).thenReturn(response);

                List<OrderDTOResponse> result = orderService.findAll();

                assertEquals(List.of(response), result);
                verify(orderRepository).findAll();
                verify(orderMapper).toResponse(order);
        }

        @Test
        void shouldReturnOrderById() {
                OrderEntity order = new OrderEntity();
                OrderDTOResponse response = new OrderDTOResponse(
                                1,
                                2,
                                "DINE IN",
                                "CASH",
                                "PENDING",
                                null,
                                null,
                                List.of());
                when(orderRepository.findById(1))
                                .thenReturn(Optional.of(order));
                when(orderMapper.toResponse(order))
                                .thenReturn(response);
                OrderDTOResponse result = orderService.findById(1);
                assertEquals(response, result);
                verify(orderRepository).findById(1);
                verify(orderMapper).toResponse(order);
        }

        @Test
        void shouldReturnNotFoundWhenOrderDoesNotExist() {
                when(orderRepository.findById(99))
                                .thenReturn(Optional.empty());
                ResponseStatusException exception = assertThrows(
                                ResponseStatusException.class,
                                () -> orderService.findById(99));
                assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
                verifyNoInteractions(orderMapper);
        }
}
