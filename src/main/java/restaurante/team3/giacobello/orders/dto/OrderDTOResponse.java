package restaurante.team3.giacobello.orders.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public record OrderDTOResponse(
        Integer id,
        Integer tabletId,
        String orderTypeName,
        String paymentMethodName,
        String statusName,
        BigDecimal totalAmount,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        List<OrderItemDTOResponse> items) {
}
