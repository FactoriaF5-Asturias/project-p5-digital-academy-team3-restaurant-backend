package restaurante.team3.giacobello.orders.service;

import java.math.BigDecimal;
import java.util.List;

import restaurante.team3.giacobello.product.entity.ProductEntity;

public record OrderPricing(List<PricedLine> lines, BigDecimal total) {

    public record PricedLine(
            ProductEntity product,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal subtotal) {
    }
}
