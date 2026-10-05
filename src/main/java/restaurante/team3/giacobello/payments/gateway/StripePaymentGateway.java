package restaurante.team3.giacobello.payments.gateway;

public interface StripePaymentGateway {

    String CURRENCY = "eur";

    PaymentIntentCreated createPaymentIntent(long amountInCents, String currency);

    PaymentIntentDetails retrievePaymentIntent(String paymentIntentId);
}
