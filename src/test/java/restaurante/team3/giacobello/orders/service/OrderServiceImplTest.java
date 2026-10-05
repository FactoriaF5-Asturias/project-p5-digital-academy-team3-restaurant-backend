package restaurante.team3.giacobello.orders.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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
import restaurante.team3.giacobello.payments.exceptions.PaymentGatewayException;
import restaurante.team3.giacobello.payments.exceptions.PaymentNotFoundException;
import restaurante.team3.giacobello.payments.exceptions.PaymentsNotConfiguredException;
import restaurante.team3.giacobello.payments.gateway.PaymentIntentDetails;
import restaurante.team3.giacobello.payments.gateway.StripePaymentGateway;
import restaurante.team3.giacobello.product.entity.ProductEntity;
import restaurante.team3.giacobello.product.repository.ProductRepository;
import restaurante.team3.giacobello.tablets.repository.TabletRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
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

        @Mock
        private StripePaymentGateway paymentGateway;

        @Mock
        private PlatformTransactionManager transactionManager;

        @Spy
        private OrderTotalCalculator orderTotalCalculator = new OrderTotalCalculator();

        @InjectMocks
        private OrderServiceImpl orderService;

        @Test
        void shouldNotCreateInvoiceWhenOrderIsCreated() {
                ProductEntity product = org.mockito.Mockito.mock(ProductEntity.class);
                OrderCreateDTORequest request = new OrderCreateDTORequest(
                                2,
                                "DINE IN",
                                "CASH",
                                List.of(new OrderItemCreateDTORequest(4, 2)),
                                null);
                OrderDTOResponse response = new OrderDTOResponse(
                                7,
                                2,
                                "DINE IN",
                                "CASH",
                                "PENDING",
                                new java.math.BigDecimal("25.00"),
                                null,
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

                ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
                verify(orderRepository).save(orderCaptor.capture());
                assertNull(orderCaptor.getValue().getPaidAt());
                verifyNoInteractions(invoiceRepository);
        }

        @Test
        void shouldCreateTakeawayOrderWithoutInvoice() {
                ProductEntity product = org.mockito.Mockito.mock(ProductEntity.class);
                OrderCreateDTORequest request = new OrderCreateDTORequest(
                                2,
                                "TAKEAWAY",
                                "CASH",
                                List.of(new OrderItemCreateDTORequest(4, 2)),
                                null);
                OrderDTOResponse response = new OrderDTOResponse(
                                7,
                                2,
                                "TAKEAWAY",
                                "CASH",
                                "PENDING",
                                new BigDecimal("25.00"),
                                null,
                                null,
                                List.of());

                when(tabletRepository.existsById(2)).thenReturn(true);
                when(productRepository.findAllById(List.of(4))).thenReturn(List.of(product));
                when(product.getId()).thenReturn(4);
                when(product.getPrice()).thenReturn(new BigDecimal("12.50"));
                when(product.getStatus()).thenReturn(true);
                doAnswer(invocation -> {
                        OrderEntity order = invocation.getArgument(0);
                        order.setId(7);
                        return order;
                }).when(orderRepository).save(any(OrderEntity.class));
                when(orderMapper.toResponse(any(OrderEntity.class))).thenReturn(response);

                OrderDTOResponse result = orderService.create(request);

                assertEquals("TAKEAWAY", result.orderTypeName());
                ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
                verify(orderRepository).save(orderCaptor.capture());
                assertEquals("TAKEAWAY", orderCaptor.getValue().getOrderTypeName());
                assertEquals("CASH", orderCaptor.getValue().getPaymentMethodName());
                assertEquals(new BigDecimal("25.00"), orderCaptor.getValue().getTotalAmount());
                verifyNoInteractions(invoiceRepository);
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

        @Test
        void shouldReturnNotFoundWhenPayingAMissingOrder() {
                when(orderRepository.findByIdForUpdate(99)).thenReturn(Optional.empty());

                ResponseStatusException exception = assertThrows(
                                ResponseStatusException.class,
                                () -> orderService.pay(99));

                assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
                verifyNoInteractions(invoiceRepository);
        }

        @Test
        void shouldReturnConflictWhenOrderIsAlreadyPaid() {
                OrderEntity order = orderWith(7, "COMPLETED");
                order.setPaidAt(LocalDateTime.of(2026, 9, 28, 13, 0));
                when(orderRepository.findByIdForUpdate(7)).thenReturn(Optional.of(order));

                ResponseStatusException exception = assertThrows(
                                ResponseStatusException.class,
                                () -> orderService.pay(7));

                assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
                verifyNoInteractions(invoiceRepository);
        }

        @Test
        void shouldReturnConflictWhenOrderIsCancelled() {
                OrderEntity order = orderWith(7, "CANCELLED");
                when(orderRepository.findByIdForUpdate(7)).thenReturn(Optional.of(order));

                ResponseStatusException exception = assertThrows(
                                ResponseStatusException.class,
                                () -> orderService.pay(7));

                assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
                assertNull(order.getPaidAt());
                verifyNoInteractions(invoiceRepository);
        }

        @Test
        void shouldMarkOrderAsPaidAndIssueItsInvoice() {
                OrderEntity order = orderWith(7, "COMPLETED");
                OrderDTOResponse response = new OrderDTOResponse(
                                7, 2, "DINE IN", "CASH", "COMPLETED",
                                new BigDecimal("25.00"), null, null, List.of());
                when(orderRepository.findByIdForUpdate(7)).thenReturn(Optional.of(order));
                when(orderRepository.save(order)).thenReturn(order);
                when(orderMapper.toResponse(order)).thenReturn(response);

                OrderDTOResponse result = orderService.pay(7);

                assertEquals(response, result);
                assertNotNull(order.getPaidAt());
                assertEquals("COMPLETED", order.getStatusName());
                ArgumentCaptor<InvoiceEntity> invoiceCaptor = ArgumentCaptor.forClass(InvoiceEntity.class);
                verify(invoiceRepository).save(invoiceCaptor.capture());
                InvoiceEntity invoice = invoiceCaptor.getValue();
                assertEquals(7, invoice.getOrderId());
                assertEquals("INV-7", invoice.getInvoiceNumber());
                assertEquals(new BigDecimal("25.00"), invoice.getTotalAmount());
                assertEquals(order.getPaidAt(), invoice.getIssuedAt());
        }

        @Test
        void shouldCreatePaidOrderAndIssueInvoiceWhenPaymentIntentSucceeded() {
                stubTablet();
                stubProduct("12.50", true);
                stubSavedOrder();
                when(paymentGateway.retrievePaymentIntent("pi_1"))
                                .thenReturn(new PaymentIntentDetails("pi_1", "succeeded", 2500, "eur"));

                OrderDTOResponse result = orderService.create(cardRequest("pi_1"));

                assertEquals(7, result.id());
                ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
                verify(orderRepository).save(orderCaptor.capture());
                OrderEntity saved = orderCaptor.getValue();
                assertNotNull(saved.getPaidAt());
                assertEquals("PENDING", saved.getStatusName());
                assertEquals("pi_1", saved.getStripePaymentIntentId());
                ArgumentCaptor<InvoiceEntity> invoiceCaptor = ArgumentCaptor.forClass(InvoiceEntity.class);
                verify(invoiceRepository).save(invoiceCaptor.capture());
                assertEquals("INV-7", invoiceCaptor.getValue().getInvoiceNumber());
                assertEquals(new BigDecimal("25.00"), invoiceCaptor.getValue().getTotalAmount());
                assertEquals(saved.getPaidAt(), invoiceCaptor.getValue().getIssuedAt());
                verify(paymentGateway, never()).refund(anyString());
        }

        @Test
        void shouldRejectWithPaymentRequiredWithoutRefundWhenPaymentIntentHasNotSucceeded() {
                stubTablet();
                when(paymentGateway.retrievePaymentIntent("pi_1"))
                                .thenReturn(new PaymentIntentDetails("pi_1", "requires_payment_method", 2500, "eur"));

                ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                                () -> orderService.create(cardRequest("pi_1")));

                assertEquals(HttpStatus.PAYMENT_REQUIRED.value(), ex.getStatusCode().value());
                verify(paymentGateway, never()).refund(anyString());
                verify(orderRepository, never()).save(any(OrderEntity.class));
                verifyNoInteractions(invoiceRepository, productRepository);
        }

        @Test
        void shouldRefundAndRejectWhenPaidAmountDoesNotMatchOrderTotal() {
                stubTablet();
                stubProduct("12.50", true);
                when(paymentGateway.retrievePaymentIntent("pi_1"))
                                .thenReturn(new PaymentIntentDetails("pi_1", "succeeded", 2400, "eur"));

                ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                                () -> orderService.create(cardRequest("pi_1")));

                assertEquals(HttpStatus.CONFLICT.value(), ex.getStatusCode().value());
                assertEquals("El importe no coincide con el pedido; el pago se ha devuelto", ex.getReason());
                verify(paymentGateway).refund("pi_1");
                verify(orderRepository, never()).save(any(OrderEntity.class));
                verifyNoInteractions(invoiceRepository);
        }

        @Test
        void shouldRefundAndRejectWhenPaidCurrencyIsNotEuro() {
                stubTablet();
                stubProduct("12.50", true);
                when(paymentGateway.retrievePaymentIntent("pi_1"))
                                .thenReturn(new PaymentIntentDetails("pi_1", "succeeded", 2500, "usd"));

                ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                                () -> orderService.create(cardRequest("pi_1")));

                assertEquals(HttpStatus.CONFLICT.value(), ex.getStatusCode().value());
                verify(paymentGateway).refund("pi_1");
        }

        @Test
        void shouldRefundAndRejectWhenAProductIsInactive() {
                stubTablet();
                stubProduct("12.50", false);
                when(paymentGateway.retrievePaymentIntent("pi_1"))
                                .thenReturn(new PaymentIntentDetails("pi_1", "succeeded", 2500, "eur"));

                ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                                () -> orderService.create(cardRequest("pi_1")));

                assertEquals(HttpStatus.CONFLICT.value(), ex.getStatusCode().value());
                assertTrue(ex.getReason().endsWith("; el pago se ha devuelto"));
                verify(paymentGateway).refund("pi_1");
                verify(orderRepository, never()).save(any(OrderEntity.class));
        }

        @Test
        void shouldRefundAndRejectWhenAProductDoesNotExist() {
                stubTablet();
                when(productRepository.findAllById(List.of(4))).thenReturn(List.of());
                when(paymentGateway.retrievePaymentIntent("pi_1"))
                                .thenReturn(new PaymentIntentDetails("pi_1", "succeeded", 2500, "eur"));

                ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                                () -> orderService.create(cardRequest("pi_1")));

                assertEquals(HttpStatus.BAD_REQUEST.value(), ex.getStatusCode().value());
                verify(paymentGateway).refund("pi_1");
        }

        @Test
        void shouldRefundAndRejectWhenItemsAreInvalidAfterTheIntentSucceeded() {
                stubTablet();
                when(paymentGateway.retrievePaymentIntent("pi_1"))
                                .thenReturn(new PaymentIntentDetails("pi_1", "succeeded", 2500, "eur"));
                OrderCreateDTORequest request = new OrderCreateDTORequest(
                                2, "DINE IN", "CARD",
                                List.of(new OrderItemCreateDTORequest(4, 1), new OrderItemCreateDTORequest(4, 1)),
                                "pi_1");

                ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                                () -> orderService.create(request));

                assertEquals(HttpStatus.BAD_REQUEST.value(), ex.getStatusCode().value());
                verify(paymentGateway).refund("pi_1");
        }

        @Test
        void shouldRefundAndRejectWhenTheTotalExceedsTheCardLimit() {
                stubTablet();
                stubProduct("999999.99", true);
                when(paymentGateway.retrievePaymentIntent("pi_1"))
                                .thenReturn(new PaymentIntentDetails("pi_1", "succeeded", 2500, "eur"));

                ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                                () -> orderService.create(cardRequest("pi_1")));

                assertEquals(HttpStatus.BAD_REQUEST.value(), ex.getStatusCode().value());
                verify(paymentGateway).refund("pi_1");
        }

        @Test
        void shouldStillReturnTheOriginalErrorWhenTheRefundFails() {
                stubTablet();
                stubProduct("12.50", true);
                when(paymentGateway.retrievePaymentIntent("pi_1"))
                                .thenReturn(new PaymentIntentDetails("pi_1", "succeeded", 2400, "eur"));
                doThrow(new PaymentGatewayException(new RuntimeException("boom")))
                                .when(paymentGateway).refund("pi_1");

                ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                                () -> orderService.create(cardRequest("pi_1")));

                assertEquals(HttpStatus.CONFLICT.value(), ex.getStatusCode().value());
                assertEquals("El importe no coincide con el pedido", ex.getReason());
        }

        @Test
        void shouldRejectWithoutRefundWhenPaymentIntentWasAlreadyUsedByAnotherOrder() {
                stubTablet();
                when(orderRepository.existsByStripePaymentIntentId("pi_1")).thenReturn(true);

                ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                                () -> orderService.create(cardRequest("pi_1")));

                assertEquals(HttpStatus.CONFLICT.value(), ex.getStatusCode().value());
                verifyNoInteractions(paymentGateway, invoiceRepository);
                verify(orderRepository, never()).save(any(OrderEntity.class));
        }

        @Test
        void shouldTranslateUniqueConstraintRaceToConflictWithoutRefund() {
                stubTablet();
                stubProduct("12.50", true);
                when(paymentGateway.retrievePaymentIntent("pi_1"))
                                .thenReturn(new PaymentIntentDetails("pi_1", "succeeded", 2500, "eur"));
                when(orderRepository.save(any(OrderEntity.class)))
                                .thenThrow(new DataIntegrityViolationException("uq_orders_stripe_payment_intent_id"));

                ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                                () -> orderService.create(cardRequest("pi_1")));

                assertEquals(HttpStatus.CONFLICT.value(), ex.getStatusCode().value());
                verify(paymentGateway, never()).refund(anyString());
                verifyNoInteractions(invoiceRepository);
        }

        @Test
        void shouldNotSwallowUnrelatedIntegrityViolationsOfUnpaidOrders() {
                stubTablet();
                stubProduct("12.50", true);
                when(orderRepository.save(any(OrderEntity.class)))
                                .thenThrow(new DataIntegrityViolationException("other"));

                assertThrows(DataIntegrityViolationException.class, () -> orderService.create(new OrderCreateDTORequest(
                                2, "DINE IN", "CASH", List.of(new OrderItemCreateDTORequest(4, 2)), null)));
        }

        @Test
        void shouldRejectPaymentIntentWithCashPaymentMethod() {
                OrderCreateDTORequest request = new OrderCreateDTORequest(
                                2, "DINE IN", "CASH", List.of(new OrderItemCreateDTORequest(4, 2)), "pi_1");

                ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                                () -> orderService.create(request));

                assertEquals(HttpStatus.BAD_REQUEST.value(), ex.getStatusCode().value());
                verifyNoInteractions(paymentGateway, orderRepository, invoiceRepository);
        }

        @Test
        void shouldRejectBlankPaymentIntentId() {
                OrderCreateDTORequest request = new OrderCreateDTORequest(
                                2, "DINE IN", "CARD", List.of(new OrderItemCreateDTORequest(4, 2)), "  ");

                ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                                () -> orderService.create(request));

                assertEquals(HttpStatus.BAD_REQUEST.value(), ex.getStatusCode().value());
                verifyNoInteractions(paymentGateway, orderRepository, invoiceRepository);
        }

        @Test
        void shouldPropagateWhenStripeIsNotConfiguredWithoutRefund() {
                stubTablet();
                when(paymentGateway.retrievePaymentIntent("pi_1")).thenThrow(new PaymentsNotConfiguredException());

                assertThrows(PaymentsNotConfiguredException.class, () -> orderService.create(cardRequest("pi_1")));
                verify(paymentGateway, never()).refund(anyString());
                verify(orderRepository, never()).save(any(OrderEntity.class));
        }

        @Test
        void shouldPropagateUnknownPaymentIntentsWithoutRefund() {
                stubTablet();
                when(paymentGateway.retrievePaymentIntent("pi_1")).thenThrow(new PaymentNotFoundException());

                assertThrows(PaymentNotFoundException.class, () -> orderService.create(cardRequest("pi_1")));
                verify(paymentGateway, never()).refund(anyString());
        }

        @Test
        void shouldPropagateGatewayFailuresWithoutRefund() {
                stubTablet();
                when(paymentGateway.retrievePaymentIntent("pi_1"))
                                .thenThrow(new PaymentGatewayException(new RuntimeException("boom")));

                assertThrows(PaymentGatewayException.class, () -> orderService.create(cardRequest("pi_1")));
                verify(paymentGateway, never()).refund(anyString());
                verify(orderRepository, never()).save(any(OrderEntity.class));
        }

        private OrderCreateDTORequest cardRequest(String paymentIntentId) {
                return new OrderCreateDTORequest(
                                2, "DINE IN", "CARD", List.of(new OrderItemCreateDTORequest(4, 2)), paymentIntentId);
        }

        private void stubTablet() {
                when(tabletRepository.existsById(2)).thenReturn(true);
        }

        private void stubProduct(String price, boolean active) {
                ProductEntity product = org.mockito.Mockito.mock(ProductEntity.class);
                when(productRepository.findAllById(List.of(4))).thenReturn(List.of(product));
                lenient().when(product.getId()).thenReturn(4);
                lenient().when(product.getPrice()).thenReturn(new BigDecimal(price));
                lenient().when(product.getStatus()).thenReturn(active);
        }

        private void stubSavedOrder() {
                OrderDTOResponse response = new OrderDTOResponse(
                                7, 2, "DINE IN", "CARD", "PENDING",
                                new BigDecimal("25.00"), null, null, List.of());
                doAnswer(invocation -> {
                        OrderEntity order = invocation.getArgument(0);
                        order.setId(7);
                        return order;
                }).when(orderRepository).save(any(OrderEntity.class));
                when(orderMapper.toResponse(any(OrderEntity.class))).thenReturn(response);
        }

        private OrderEntity orderWith(Integer id, String statusName) {
                return new OrderEntity(
                                id, 2, "DINE IN", "CASH", statusName,
                                new BigDecimal("25.00"),
                                LocalDateTime.of(2026, 9, 28, 12, 30));
        }
}
