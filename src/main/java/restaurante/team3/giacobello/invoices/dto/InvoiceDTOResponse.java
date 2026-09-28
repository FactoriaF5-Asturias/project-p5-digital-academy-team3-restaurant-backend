package restaurante.team3.giacobello.invoices.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record InvoiceDTOResponse(
        Integer id,
        Integer orderId,
        String invoiceNumber,
        BigDecimal totalAmount,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime issuedAt) {
}