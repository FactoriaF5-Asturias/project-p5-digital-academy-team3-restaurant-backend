package restaurante.team3.giacobello.payments.gateway;

public record PaymentIntentDetails(
        String id,
        String status,
        long amountInCents,
        String currency) {
}
