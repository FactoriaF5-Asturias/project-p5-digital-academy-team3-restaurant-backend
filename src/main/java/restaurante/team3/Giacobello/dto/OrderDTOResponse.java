package restaurante.team3.Giacobello.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderDTOResponse(Integer id,
        Integer tabletId,
        String statusName,
        BigDecimal totalAmount,
        LocalDateTime createdAt) {
}
