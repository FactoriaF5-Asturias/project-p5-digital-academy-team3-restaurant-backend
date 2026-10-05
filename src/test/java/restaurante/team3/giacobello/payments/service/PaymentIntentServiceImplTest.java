package restaurante.team3.giacobello.payments.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import restaurante.team3.giacobello.orders.dto.OrderItemCreateDTORequest;
import restaurante.team3.giacobello.orders.service.OrderTotalCalculator;
import restaurante.team3.giacobello.payments.dto.PaymentIntentCreateDTORequest;
import restaurante.team3.giacobello.payments.dto.PaymentIntentDTOResponse;
import restaurante.team3.giacobello.payments.exceptions.PaymentGatewayException;
import restaurante.team3.giacobello.payments.exceptions.PaymentsNotConfiguredException;
import restaurante.team3.giacobello.payments.gateway.PaymentIntentCreated;
import restaurante.team3.giacobello.payments.gateway.StripePaymentGateway;
import restaurante.team3.giacobello.product.entity.ProductEntity;
import restaurante.team3.giacobello.product.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class PaymentIntentServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StripePaymentGateway paymentGateway;

    @Spy
    private OrderTotalCalculator orderTotalCalculator = new OrderTotalCalculator();

    @InjectMocks
    private PaymentIntentServiceImpl service;

    private ProductEntity product(Integer id, String price, boolean active) {
        ProductEntity product = mock(ProductEntity.class);
        lenient().when(product.getId()).thenReturn(id);
        lenient().when(product.getPrice()).thenReturn(new BigDecimal(price));
        lenient().when(product.getStatus()).thenReturn(active);
        return product;
    }

    private void stubProducts(List<Integer> ids, ProductEntity... products) {
        when(productRepository.findAllById(ids)).thenReturn(List.of(products));
    }

    @Test
    void createsTheIntentWithTheTotalComputedFromDatabasePricesInCents() {
        stubProducts(List.of(4, 5), product(4, "12.50", true), product(5, "3.05", true));
        when(paymentGateway.createPaymentIntent(2805, "eur"))
                .thenReturn(new PaymentIntentCreated("pi_1", "pi_1_secret"));

        PaymentIntentDTOResponse response = service.createIntent(new PaymentIntentCreateDTORequest(
                List.of(new OrderItemCreateDTORequest(4, 2), new OrderItemCreateDTORequest(5, 1))));

        assertEquals("pi_1", response.paymentIntentId());
        assertEquals("pi_1_secret", response.clientSecret());
        assertEquals(new BigDecimal("28.05"), response.amount());
        verify(paymentGateway).createPaymentIntent(2805L, "eur");
    }

    @Test
    void rejectsUnknownProducts() {
        stubProducts(List.of(9));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.createIntent(new PaymentIntentCreateDTORequest(
                        List.of(new OrderItemCreateDTORequest(9, 1)))));

        assertEquals(HttpStatus.BAD_REQUEST.value(), ex.getStatusCode().value());
        verifyNoInteractions(paymentGateway);
    }

    @Test
    void rejectsInactiveProducts() {
        stubProducts(List.of(4), product(4, "5.00", false));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.createIntent(new PaymentIntentCreateDTORequest(
                        List.of(new OrderItemCreateDTORequest(4, 1)))));

        assertEquals(HttpStatus.CONFLICT.value(), ex.getStatusCode().value());
        verifyNoInteractions(paymentGateway);
    }

    @Test
    void rejectsDuplicatedProductsWithoutTouchingTheDatabase() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.createIntent(new PaymentIntentCreateDTORequest(
                        List.of(new OrderItemCreateDTORequest(4, 1), new OrderItemCreateDTORequest(4, 2)))));

        assertEquals(HttpStatus.BAD_REQUEST.value(), ex.getStatusCode().value());
        verifyNoInteractions(productRepository, paymentGateway);
    }

    @Test
    void rejectsAmountsBelowTheStripeMinimum() {
        stubProducts(List.of(4), product(4, "0.40", true));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.createIntent(new PaymentIntentCreateDTORequest(
                        List.of(new OrderItemCreateDTORequest(4, 1)))));

        assertEquals(HttpStatus.BAD_REQUEST.value(), ex.getStatusCode().value());
        verifyNoInteractions(paymentGateway);
    }

    @Test
    void propagatesWhenStripeIsNotConfigured() {
        stubProducts(List.of(4), product(4, "5.00", true));
        when(paymentGateway.createPaymentIntent(anyLong(), anyString()))
                .thenThrow(new PaymentsNotConfiguredException());

        assertThrows(PaymentsNotConfiguredException.class,
                () -> service.createIntent(new PaymentIntentCreateDTORequest(
                        List.of(new OrderItemCreateDTORequest(4, 1)))));
    }

    @Test
    void propagatesGatewayFailures() {
        stubProducts(List.of(4), product(4, "5.00", true));
        PaymentGatewayException failure = new PaymentGatewayException(new RuntimeException("boom"));
        when(paymentGateway.createPaymentIntent(500L, "eur")).thenThrow(failure);

        PaymentGatewayException thrown = assertThrows(PaymentGatewayException.class,
                () -> service.createIntent(new PaymentIntentCreateDTORequest(
                        List.of(new OrderItemCreateDTORequest(4, 1)))));

        assertSame(failure, thrown);
    }
}
