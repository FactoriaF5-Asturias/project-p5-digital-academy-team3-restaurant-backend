package restaurante.team3.giacobello.invoices.dto;

import java.math.BigDecimal;

public record SalesTotalsDTOResponse(
        BigDecimal daily,
        BigDecimal monthly,
        BigDecimal quarterly,
        BigDecimal yearly) {
}
