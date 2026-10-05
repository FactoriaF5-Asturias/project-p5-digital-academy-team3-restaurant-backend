package restaurante.team3.giacobello.orders.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import restaurante.team3.giacobello.orders.dto.OrderItemCreateDTORequest;
import restaurante.team3.giacobello.orders.service.OrderPricing.PricedLine;
import restaurante.team3.giacobello.product.entity.ProductEntity;

@Component
public class OrderTotalCalculator {

    private static final BigDecimal MAX_TOTAL = new BigDecimal("99999999.99");

    public OrderPricing calculate(
            List<OrderItemCreateDTORequest> items,
            Map<Integer, ProductEntity> products) {
        validateItems(items);

        List<PricedLine> lines = new ArrayList<>();
        BigDecimal total = new BigDecimal("0.00");

        for (OrderItemCreateDTORequest item : items) {
            ProductEntity product = products.get(item.productId());

            if (product == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No existe el producto " + item.productId());
            }

            if (!Boolean.TRUE.equals(product.getStatus())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "El producto " + product.getId()
                                + " no está disponible");
            }

            BigDecimal unitPrice = product.getPrice();

            if (unitPrice == null || unitPrice.signum() < 0) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "No se pudo calcular el importe del pedido");
            }

            BigDecimal subtotal = unitPrice.multiply(
                    BigDecimal.valueOf(item.quantity()));

            total = total.add(subtotal);

            if (total.compareTo(MAX_TOTAL) > 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El importe supera el máximo permitido");
            }

            lines.add(new PricedLine(product, item.quantity(), unitPrice, subtotal));
        }

        return new OrderPricing(lines, total);
    }

    public void validateItems(List<OrderItemCreateDTORequest> items) {
        if (items == null || items.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El pedido debe contener productos");
        }

        var seenProducts = new HashSet<Integer>();

        for (OrderItemCreateDTORequest item : items) {
            if (item == null
                    || item.productId() == null
                    || item.productId() <= 0
                    || item.quantity() == null
                    || item.quantity() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Producto o cantidad inválidos");
            }

            if (!seenProducts.add(item.productId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No se puede repetir un producto; aumenta su cantidad");
            }
        }
    }
}
