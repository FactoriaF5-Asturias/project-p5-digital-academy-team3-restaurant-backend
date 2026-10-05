package restaurante.team3.giacobello.payments.dto;

import java.math.BigDecimal;

public record PaymentIntentDTOResponse(
        String paymentIntentId,
        String clientSecret,
        BigDecimal amount) {
}
