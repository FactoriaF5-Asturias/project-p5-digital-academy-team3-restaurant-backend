package restaurante.team3.giacobello.orders.dto;

import java.math.BigDecimal;

public record OrderItemDTOResponse(
        Integer id,
        Integer productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal) {
}
