package restaurante.team3.giacobello.payments.gateway;

public interface StripePaymentGateway {

    String CURRENCY = "eur";
    long MAX_AMOUNT_IN_CENTS = 99_999_999L;

    PaymentIntentCreated createPaymentIntent(long amountInCents, String currency);

    PaymentIntentDetails retrievePaymentIntent(String paymentIntentId);
}
