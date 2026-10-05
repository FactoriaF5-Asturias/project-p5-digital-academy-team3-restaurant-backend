package restaurante.team3.giacobello.orders.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import restaurante.team3.giacobello.orders.dto.OrderItemCreateDTORequest;
import restaurante.team3.giacobello.product.entity.ProductEntity;

class OrderTotalCalculatorTest {

    private final OrderTotalCalculator calculator = new OrderTotalCalculator();

    private ProductEntity product(Integer id, String price, Boolean active) {
        ProductEntity product = mock(ProductEntity.class);
        when(product.getId()).thenReturn(id);
        when(product.getPrice()).thenReturn(price == null ? null : new BigDecimal(price));
        when(product.getStatus()).thenReturn(active);
        return product;
    }

    private HttpStatus statusOf(List<OrderItemCreateDTORequest> items, Map<Integer, ProductEntity> products) {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> calculator.calculate(items, products));
        return HttpStatus.valueOf(ex.getStatusCode().value());
    }

    @Test
    void calculatesLinesAndTotal() {
        Map<Integer, ProductEntity> products = Map.of(
                1, product(1, "12.50", true),
                2, product(2, "3.00", true));

        OrderPricing pricing = calculator.calculate(
                List.of(new OrderItemCreateDTORequest(1, 2), new OrderItemCreateDTORequest(2, 1)),
                products);

        assertEquals(new BigDecimal("28.00"), pricing.total());
        assertEquals(2, pricing.lines().size());
        assertEquals(new BigDecimal("25.00"), pricing.lines().get(0).subtotal());
        assertEquals(new BigDecimal("12.50"), pricing.lines().get(0).unitPrice());
        assertEquals(2, pricing.lines().get(0).quantity());
    }

    @Test
    void rejectsUnknownProduct() {
        assertEquals(HttpStatus.BAD_REQUEST,
                statusOf(List.of(new OrderItemCreateDTORequest(9, 1)), Map.of()));
    }

    @Test
    void rejectsInactiveProduct() {
        assertEquals(HttpStatus.CONFLICT,
                statusOf(List.of(new OrderItemCreateDTORequest(1, 1)),
                        Map.of(1, product(1, "5.00", false))));
    }

    @Test
    void rejectsProductWithoutValidPrice() {
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                statusOf(List.of(new OrderItemCreateDTORequest(1, 1)),
                        Map.of(1, product(1, null, true))));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                statusOf(List.of(new OrderItemCreateDTORequest(1, 1)),
                        Map.of(1, product(1, "-1.00", true))));
    }

    @Test
    void rejectsTotalAboveMaximum() {
        assertEquals(HttpStatus.BAD_REQUEST,
                statusOf(List.of(new OrderItemCreateDTORequest(1, 2)),
                        Map.of(1, product(1, "99999999.99", true))));
    }

    @Test
    void rejectsEmptyOrNullItems() {
        assertEquals(HttpStatus.BAD_REQUEST, statusOf(List.of(), Map.of()));
        assertEquals(HttpStatus.BAD_REQUEST, statusOf(null, Map.of()));
    }

    @Test
    void rejectsInvalidItems() {
        assertEquals(HttpStatus.BAD_REQUEST,
                statusOf(Arrays.asList((OrderItemCreateDTORequest) null), Map.of()));
        assertEquals(HttpStatus.BAD_REQUEST,
                statusOf(List.of(new OrderItemCreateDTORequest(null, 1)), Map.of()));
        assertEquals(HttpStatus.BAD_REQUEST,
                statusOf(List.of(new OrderItemCreateDTORequest(0, 1)), Map.of()));
        assertEquals(HttpStatus.BAD_REQUEST,
                statusOf(List.of(new OrderItemCreateDTORequest(1, null)), Map.of()));
        assertEquals(HttpStatus.BAD_REQUEST,
                statusOf(List.of(new OrderItemCreateDTORequest(1, 0)), Map.of()));
    }

    @Test
    void rejectsDuplicatedProduct() {
        assertEquals(HttpStatus.BAD_REQUEST,
                statusOf(List.of(new OrderItemCreateDTORequest(1, 1), new OrderItemCreateDTORequest(1, 2)),
                        Map.of(1, product(1, "5.00", true))));
    }
}
