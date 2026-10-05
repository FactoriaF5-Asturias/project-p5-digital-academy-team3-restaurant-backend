package restaurante.team3.giacobello.orders.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

class OrderPricingTest {

    @Test
    void convertsTheTotalToCents() {
        assertEquals(2805L, new OrderPricing(List.of(), new BigDecimal("28.05")).totalInCents());
        assertEquals(1200L, new OrderPricing(List.of(), new BigDecimal("12")).totalInCents());
    }

    @Test
    void refusesToRoundTotalsWithMoreThanTwoDecimals() {
        assertThrows(ArithmeticException.class,
                () -> new OrderPricing(List.of(), new BigDecimal("1.005")).totalInCents());
    }
}
