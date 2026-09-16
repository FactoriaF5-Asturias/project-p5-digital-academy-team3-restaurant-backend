package restaurante.team3.Giacobello.orders.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDTOResponse(
        Integer id,
        Integer tabletId,
        String orderTypeName,
        String paymentMethodName,
        String statusName,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        List<OrderItemDTOResponse> items) {
}
