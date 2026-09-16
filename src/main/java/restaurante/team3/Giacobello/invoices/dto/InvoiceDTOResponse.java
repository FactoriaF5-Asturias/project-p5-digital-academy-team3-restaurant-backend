package restaurante.team3.Giacobello.invoices.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvoiceDTOResponse(
        Integer id,
        Integer orderId,
        String invoiceNumber,
        BigDecimal totalAmount,
        LocalDateTime issuedAt) {
}